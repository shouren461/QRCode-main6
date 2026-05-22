package com.drojian.qrcode.scanresultlib.supplement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

final class BookResultInfoRetriever extends SupplementalInfoRetriever {
    private final String isbn;
    private final String source;


    BookResultInfoRetriever(String isbn) {
        super();
        this.isbn = isbn;
        this.source = "";//Google
    }

    @Override
    void retrieveSupplementalInfo() throws IOException {

        CharSequence contents = HttpHelper.downloadViaHttp("https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn,
                HttpHelper.ContentType.JSON);

        if (contents.length() == 0) {
            return;
        }

        String title;
        String pages;
        Collection<String> authors = null;

        try {
            JSONObject topLevel = (JSONObject) new JSONTokener(contents.toString()).nextValue();
            JSONArray items = topLevel.optJSONArray("items");
            if (items == null || items.isNull(0)) {
                return;
            }

            JSONObject volumeInfo = ((JSONObject) items.get(0)).getJSONObject("volumeInfo");

            title = volumeInfo.optString("title");
            pages = volumeInfo.optString("pageCount");

            JSONArray authorsArray = volumeInfo.optJSONArray("authors");
            if (authorsArray != null && !authorsArray.isNull(0)) {
                authors = new ArrayList<>(authorsArray.length());
                for (int i = 0; i < authorsArray.length(); i++) {
                    authors.add(authorsArray.getString(i));
                }
            }
        } catch (JSONException e) {
            throw new IOException(e);
        }

        Collection<String> newTexts = new ArrayList<>();
        maybeAddText(title, newTexts);
        maybeAddTextSeries(authors, newTexts);
        maybeAddText(pages.isEmpty() ? null : pages + "pp.", newTexts);

        String baseBookUri = "https://www.google." + LocaleManager.getBookSearchCountryTLD()
                + "/search?tbm=bks&q=";//&source=zxing

        append(isbn, source, newTexts.toArray(EMPTY_STR_ARRAY), baseBookUri + isbn);
    }

}
