/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.drojian.qrcode.scanresultlib.supplement;

import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.util.Log;

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel;
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat;
import com.drojian.qrcode.scanlib.scan.parse.format.ParseExpandedProductModel;
import com.drojian.qrcode.scanlib.scan.parse.format.ParseISBNModel;
import com.drojian.qrcode.scanlib.scan.parse.format.ParseProductModel;
import com.drojian.qrcode.scanlib.scan.parse.format.ParseURIModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RejectedExecutionException;

/**
 * Superclass of implementations which can asynchronously retrieve more information
 * about a barcode scan.
 */
public abstract class SupplementalInfoRetriever extends AsyncTask<Object, Object, Object> {

    private static final String TAG = "SupplementalInfo";

    static final String[] EMPTY_STR_ARRAY = new String[0];

    private final Collection<Spannable> newContents;
    private final Collection<String[]> newHistories;
    private static SupplementListener supplementListener;
    private static int onPostExecuteCalledTimes = 0;
    private static BaseParseModel parseModel;
    static boolean hasResult = false;

    public static void maybeInvokeRetrieval(BaseParseModel baseParseModel, SupplementListener supplementListener) {
        try {
            onPostExecuteCalledTimes = 0;
            hasResult = false;
            parseModel = baseParseModel;
            SupplementalInfoRetriever.supplementListener = supplementListener;
            if (baseParseModel.getParsedFormat() == ParsedFormat.URI) {
                SupplementalInfoRetriever uriRetriever = new URIResultInfoRetriever((ParseURIModel) baseParseModel);
                uriRetriever.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                SupplementalInfoRetriever titleRetriever = new TitleRetriever((ParseURIModel) baseParseModel);
                titleRetriever.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (baseParseModel.getParsedFormat() == ParsedFormat.PRODUCT) {
                String productID = "";
                if (baseParseModel instanceof ParseProductModel) {
                    productID = ((ParseProductModel) baseParseModel).getNormalizedProductID();
                }
                if (baseParseModel instanceof ParseExpandedProductModel) {
                    productID = ((ParseExpandedProductModel) baseParseModel).getRawText();
                }
                SupplementalInfoRetriever productRetriever = new ProductResultInfoRetriever(productID);
                productRetriever.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (baseParseModel.getParsedFormat() == ParsedFormat.ISBN) {
                String isbn = ((ParseISBNModel) baseParseModel).getIsbn();
                SupplementalInfoRetriever productInfoRetriever = new ProductResultInfoRetriever(isbn);
                productInfoRetriever.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                SupplementalInfoRetriever bookInfoRetriever = new BookResultInfoRetriever(isbn);
                bookInfoRetriever.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (RejectedExecutionException ree) {
            // do nothing
        }
    }

    SupplementalInfoRetriever() {
        newContents = new ArrayList<>();
        newHistories = new ArrayList<>();
    }

    @Override
    public final Object doInBackground(Object... args) {
        try {
            retrieveSupplementalInfo();
        } catch (Exception e) {
            Log.w(TAG, e);
        }
        return null;
    }

    @Override
    protected final void onPostExecute(Object arg) {
        onPostExecuteCalledTimes++;
        StringBuilder moreInfo = new StringBuilder();
        for (CharSequence content : newContents) {
            moreInfo.append(content);
        }
        try {
            for (String[] text : newHistories) {
                hasResult = true;
                if (supplementListener != null) {
                    supplementListener.onGetMessage(text[0], text[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!hasResult) {
            if (parseModel.getParsedFormat() == ParsedFormat.PRODUCT && onPostExecuteCalledTimes > 0) {
                onPostExecuteCalledTimes = 0;
                if (supplementListener != null) {
                    supplementListener.onGetMessage("", "");
                }
            } else if (parseModel.getParsedFormat() == ParsedFormat.ISBN && onPostExecuteCalledTimes > 1) {
                onPostExecuteCalledTimes = 0;
                if (supplementListener != null) {
                    supplementListener.onGetMessage("", "");
                }
            }
        }

    }

    abstract void retrieveSupplementalInfo() throws IOException;

    final void append(String itemID, String source, String[] newTexts, String linkURL) {

        StringBuilder newTextCombined = new StringBuilder();

        if (source != null) {
            newTextCombined.append(source).append(' ');
        }

        int linkStart = newTextCombined.length();

        boolean first = true;
        for (String newText : newTexts) {
            if (first) {
                newTextCombined.append(newText);
                first = false;
            } else {
                newTextCombined.append(" [");
                newTextCombined.append(newText);
                newTextCombined.append(']');
            }
        }

        int linkEnd = newTextCombined.length();

        String newText = newTextCombined.toString();
        Spannable content = new SpannableString(newText + "\n\n");
        if (linkURL != null) {
            // Strangely, some Android browsers don't seem to register to handle HTTP:// or HTTPS://.
            // Lower-case these as it should always be OK to lower-case these schemes.
            if (linkURL.startsWith("HTTP://")) {
                linkURL = "http" + linkURL.substring(4);
            } else if (linkURL.startsWith("HTTPS://")) {
                linkURL = "https" + linkURL.substring(5);
            }
            content.setSpan(new URLSpan(linkURL), linkStart, linkEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        newContents.add(content);
        newHistories.add(new String[]{itemID, newText});
    }

    static void maybeAddText(String text, Collection<String> texts) {
        if (text != null && !text.isEmpty()) {
            texts.add(text);
        }
    }

    static void maybeAddTextSeries(Collection<String> textSeries, Collection<String> texts) {
        if (textSeries != null && !textSeries.isEmpty()) {
            boolean first = true;
            StringBuilder authorsText = new StringBuilder();
            for (String author : textSeries) {
                if (first) {
                    first = false;
                } else {
                    authorsText.append(", ");
                }
                authorsText.append(author);
            }
            texts.add(authorsText.toString());
        }
    }

}
