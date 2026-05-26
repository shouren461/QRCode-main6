package qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.input;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.drojian.qrcode.createlib.create.format.CreateCalendarModel;
import com.drojian.qrcode.utillib.extension.EditTextKt;
import com.drojian.qrcode.utillib.utils.DateUtil;
import com.drojian.qrcode.utillib.utils.SoftInputUtil;
import com.drojian.qrcode.utillib.utils.StringUtils;
import java.util.Calendar;

import qrscanner.barcodescanner.barcodereader.qrcodereader.R;
import qrscanner.barcodescanner.barcodereader.qrcodereader.util.AnalyticsHelper;

//日历事件二维码创建输入页面 ->支持事件标题,地点，描述，以及开始和结束时间
public class CalenderInputActivity extends BaseCreateActivity implements View.OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener {
    private final int DATE_START = 1; // 标记正在设置开始时间
    private final int DATE_END = 2;   // 标记正在设置结束时间

    private ImageView iv_back, iv_icon, iv_more, mCreateImageView;
    private TextView tv_title, tv_location, tv_start_date, tv_end_date, mCreateTextView;
    private EditText et_title, et_location, et_description;
    private Switch switchAllday;

    private boolean isAllDay = false; // 是否为全天事件
    private boolean is_show_location = false; // 是否已展开地点输入框
    private Calendar calendar_start, calendar_end, calendar_temp; //开始时间，结束时间,临时时间

    @Override
    protected int getLayout() {
        return R.layout.activity_input_calender;
    }

    @Override
    protected void initData() {
        //UI 控件绑定
        iv_back = findViewById(R.id.iv_back);
        iv_icon = findViewById(R.id.iv_icon);
        tv_title = findViewById(R.id.tv_title);
        et_title = findViewById(R.id.et_title);
        iv_more = findViewById(R.id.iv_more);
        tv_location = findViewById(R.id.tv_location);
        et_location = findViewById(R.id.et_location);
        switchAllday = findViewById(R.id.switch_add_day);
        tv_start_date = findViewById(R.id.tv_start_date);
        tv_end_date = findViewById(R.id.tv_end_date);
        et_description = findViewById(R.id.et_description);
        
        // 初始化时间：默认结束时间比开始时间晚 1 小时
        calendar_start = Calendar.getInstance();
        calendar_end = Calendar.getInstance();
        calendar_end.add(Calendar.HOUR_OF_DAY, 1);
        calendar_temp = Calendar.getInstance();
        mCreateImageView = findViewById(R.id.iv_create);
        mCreateTextView = findViewById(R.id.tv_create);
        
        // 格式化标签文本
        TextView tv_start = findViewById(R.id.tv_start);
        tv_start.setText(getString(R.string.content_start).replace(":", ""));
        TextView tv_end = findViewById(R.id.tv_end);
        tv_end.setText(getString(R.string.content_end).replace(":", ""));
    }

    @Override
    protected void initView() {
        // 设置页头图标和标题
        iv_icon.setImageResource(R.drawable.vector_ic_calendar);
        iv_icon.setBackgroundResource(R.drawable.bg_creat_input_icon);
        tv_title.setText(R.string.result_calendar);
        // 初始化显示默认日期
        setDefaultDate();
    }

    @Override
    protected void initAction() {
        //设置图标点击事件
        iv_back.setOnClickListener(this);
        tv_start_date.setOnClickListener(this);
        tv_end_date.setOnClickListener(this);
        iv_more.setOnClickListener(this);
        // 监听多个输入框，只要有一个有内容，生成按钮就可用
        et_title.addTextChangedListener(this);
        et_location.addTextChangedListener(this);
        et_description.addTextChangedListener(this);
        switchAllday.setOnCheckedChangeListener(this);
        findViewById(R.id.view_create).setOnClickListener(this);
    }

    //设置"生成"按钮的可用状态和外观
    public void setCreatable(boolean creatable) {
        isCreatable = creatable;
        if (creatable) {
            mCreateTextView.setTextColor(Color.parseColor("#4880FF"));
            mCreateImageView.setImageResource(R.drawable.ic_check_blue);
        } else {
            mCreateTextView.setTextColor(Color.parseColor("#9AA7B9"));
            mCreateImageView.setImageResource(R.drawable.ic_check_black);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //调用系统软键盘
        SoftInputUtil.show(et_title);
    }

    //设置默认日期显示逻辑 ->默认开始时间为当前时间,分数向上取整到00 或30
    private void setDefaultDate() {
        //1,定义一般日期 月份+日期
        String start_date = DateUtil.INSTANCE.getMonthText(calendar_start.get(Calendar.MONTH)) + " " + calendar_start.get(Calendar.DAY_OF_MONTH) + "  ";
        String end_date = DateUtil.INSTANCE.getMonthText(calendar_end.get(Calendar.MONTH)) + " " + calendar_end.get(Calendar.DAY_OF_MONTH) + "  ";

        if (!isAllDay) {
            //2,默认实现的是非全天逻辑
               //开始时间:00-30 自动设置为 00
            String start_time_min = "00";
            if (calendar_start.getTime().getMinutes() < 30) {
                start_time_min = "00";
                calendar_start.set(Calendar.MINUTE, 00);
            } else if (calendar_start.getTime().getMinutes() < 60) {
                //31-60 向前取整为 30
                start_time_min = "30";
                calendar_start.set(Calendar.MINUTE, 30);
            }
            //结束时间:00-30 自动设置为 00
            String end_time_min = "00";
            if (calendar_end.getTime().getMinutes() < 30) {
                end_time_min = "00";
                calendar_end.set(Calendar.MINUTE, 00);
            } else if (calendar_end.getTime().getMinutes() < 60) {
                //31-60 向前取整为 30
                end_time_min = "30";
                calendar_end.set(Calendar.MINUTE, 30);
            }
            //3,拼接月份，日期，时，分
            start_date += calendar_start.get(Calendar.HOUR_OF_DAY) + ":" + start_time_min;
            end_date = calendar_end.get(Calendar.HOUR_OF_DAY) + ":" + end_time_min;

            //4,如果跨天了，结束时间也要显示日期
            if (calendar_start.get(Calendar.DAY_OF_MONTH) != calendar_end.get(Calendar.DAY_OF_MONTH)) {
                end_date = DateUtil.INSTANCE.getMonthText(calendar_end.get(Calendar.MONTH)) + " " + calendar_end.get(Calendar.DAY_OF_MONTH) + "  " + end_date;
            }
        }

        tv_start_date.setText(start_date);
        tv_end_date.setText(end_date);
    }

    //弹出日期和时间选择器 -> @param category DATA_START 或DATA_END
    private void getData(final int category) {
        //1,时间选择器回调
        final TimePickerDialog timePickerDialog = new TimePickerDialog(CalenderInputActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (category == DATE_START) {
                    calendar_start.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar_start.set(Calendar.MINUTE, minute);
                } else {
                    calendar_end.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar_end.set(Calendar.MINUTE, minute);
                }

                //1.2,自动修正：如果结束时间早于开始时间，则自动设为开始时间后一小时
                if (calendar_end.before(calendar_start)) {
                    calendar_end = (Calendar) calendar_start.clone();
                    calendar_end.set(Calendar.HOUR_OF_DAY, calendar_start.get(Calendar.HOUR_OF_DAY) + 1);
                }
                updateDate();
            }
        },      //1.3 更新 开始时间和结束时间的 分秒
                category == DATE_START ? calendar_start.get(Calendar.HOUR_OF_DAY) : calendar_end.get(Calendar.HOUR_OF_DAY),
                category == DATE_START ? calendar_start.get(Calendar.MINUTE) : calendar_end.get(Calendar.MINUTE),
                true);

        //2,日期选择器回调
        final DatePickerDialog datePickerDialog = new DatePickerDialog(CalenderInputActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //2.1 设置开始时间的年月日
                        if (category == DATE_START) {
                            calendar_start.set(Calendar.YEAR, year);
                            calendar_start.set(Calendar.MONDAY, month);
                            calendar_start.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        } else {
                            //2.2 设置结束时间的年月日
                            calendar_end.set(Calendar.YEAR, year);
                            calendar_end.set(Calendar.MONDAY, month);
                            calendar_end.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        }
                        //2.3,自动修正：如果结束时间早于开始时间，则自动设为开始时间后一小时
                        if (calendar_end.before(calendar_start)) {
                            calendar_end = (Calendar) calendar_start.clone();
                            calendar_end.set(Calendar.HOUR_OF_DAY, calendar_start.get(Calendar.HOUR_OF_DAY) + 1);
                        }
                        //2.4 如果不是全天模式，选完日期接着选时间
                        if (!isAllDay) {
                            timePickerDialog.show();
                        }
                        updateDate();
                    }
                },  //2.5 更新开始时间 和结束时间的年月日
                category == DATE_START ? calendar_start.get(Calendar.YEAR) : calendar_end.get(Calendar.YEAR),
                category == DATE_START ? calendar_start.get(Calendar.MONTH) : calendar_end.get(Calendar.MONTH),
                category == DATE_START ? calendar_start.get(Calendar.DAY_OF_MONTH) : calendar_end.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    //核心逻辑:将用户输入的各个字段组装成标准的ICalendar格式并生成二维码
    private void createQR() {
        //1,格式化日期为 YYYYMMDD
        String year_start = calendar_start.get(Calendar.YEAR) + "";
        String month_start = Integer.toString(calendar_start.get(Calendar.MONTH) + 1);
        String day_start = calendar_start.get(Calendar.DAY_OF_MONTH) + "";
        String year_end = calendar_end.get(Calendar.YEAR) + "";
        String month_end = Integer.toString(calendar_end.get(Calendar.MONTH) + 1);
        String day_end = Integer.toString(calendar_end.get(Calendar.DAY_OF_MONTH));
        
        //2,补零处理
        if (calendar_start.get(Calendar.MONTH) < 9) {
            month_start = "0" + month_start;
        }
        if (calendar_start.get(Calendar.DAY_OF_MONTH) < 10) {
            day_start = "0" + day_start;
        }

        if (calendar_end.get(Calendar.MONTH) < 9) {
            month_end = "0" + month_end;
        }
        if (calendar_end.get(Calendar.DAY_OF_MONTH) < 10) {
            day_end = "0" + day_end;
        }
        //3,拼接开始日期和结束日期的 年月日 标准格式
        String dtstart_date = year_start + month_start + day_start;
        String dtedn_date = year_end + month_end + day_end;
        
        //4,如果不是全天，追加时间部分 T HHMMSS
        if (!isAllDay) {
            String hour_start = calendar_start.get(Calendar.HOUR_OF_DAY) + "";
            String minute_start = calendar_start.get(Calendar.MINUTE) + "";
            String second_start = calendar_start.get(Calendar.SECOND) + "";
            String hour_end = calendar_end.get(Calendar.HOUR_OF_DAY) + "";
            String minute_end = calendar_end.get(Calendar.MINUTE) + "";
            String second_end = calendar_end.get(Calendar.SECOND) + "";

            if (calendar_start.get(Calendar.HOUR_OF_DAY) < 10) hour_start = "0" + hour_start;
            if (calendar_start.get(Calendar.MINUTE) < 10) minute_start = "0" + minute_start;
            if (calendar_start.get(Calendar.SECOND) < 10) second_start = "0" + second_start;
            if (calendar_end.get(Calendar.HOUR_OF_DAY) < 10) hour_end = "0" + hour_end;
            if (calendar_end.get(Calendar.MINUTE) < 10) minute_end = "0" + minute_end;
            if (calendar_end.get(Calendar.SECOND) < 10) second_end = "0" + second_end;
            //拼接开始日期和结束日期的 (年月日 + 时分秒) 标准格式
            dtstart_date = dtstart_date + "T" + hour_start + minute_start + second_start;
            dtedn_date = dtedn_date + "T" + hour_end + minute_end + second_end;
        }
        
        submitEventTracking(); // 埋点
        
        //5,构建标准日历数据模型
        baseResultModel = new CreateCalendarModel();
        ((CreateCalendarModel) baseResultModel).setSummary(EditTextKt.getEditTextString(et_title));
        ((CreateCalendarModel) baseResultModel).setDescription(EditTextKt.getEditTextString(et_description));
        ((CreateCalendarModel) baseResultModel).setLocation(EditTextKt.getEditTextString(et_location));
        ((CreateCalendarModel) baseResultModel).setStartDate(dtstart_date);
        ((CreateCalendarModel) baseResultModel).setEndDate(dtedn_date);
        
        //6,设置列表页展示标题
        baseResultModel.setShowText(getDisplayContent(EditTextKt.getEditTextString(et_title), EditTextKt.getEditTextString(et_description), EditTextKt.getEditTextString(et_location)));
        showResult(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            //1,点击返回图标
            CalenderInputActivity.this.finish();
        } else if (v.getId() == R.id.tv_start_date) {
            //2,点击开始时间图标
            getData(DATE_START);
        } else if (v.getId() == R.id.tv_end_date) {
            //3,点击结束时间图标
            getData(DATE_END);
        } else if (v.getId() == R.id.view_create) {
            //4,点击创建图标
            if (isCreatable) {
                createQR();
            } else {
                showInputNullToast();
            }
        } else if (v.getId() == R.id.iv_more) {
            //5,展开“更多”输入项（如地点）
            if (!is_show_location) {
                is_show_location = true;
                iv_more.setVisibility(View.GONE);
                tv_location.setVisibility(View.VISIBLE);
                et_location.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    //实时检查输入合法性:只要标题,地点或描述 其中一个不为空，且不全是空格，即可点击生成
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s == null)
            return;
        //1,要标题,地点或描述 其中一个不为空，且不全是空格，即可点击生成
        if ((et_title.getText().toString().length() > 0) || (et_location.getText().toString().length() > 0)
                || (et_description.getText().toString().length() > 0)) {

            if (StringUtils.isOnlySpace(et_title.getText().toString()) &&
                    StringUtils.isOnlySpace(et_location.getText().toString()) &&
                    StringUtils.isOnlySpace(et_description.getText().toString())) {
                //2,如果字符串长度大于0，都是空格键，不能生成
                setCreatable(false);
            } else {
                setCreatable(true);
            }
        } else {
            //3,如果标题，地点，描述全为空
            setCreatable(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
    //全天开关切换回调
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isAllDay = isChecked;
        updateDate();
    }
    //刷新界面上显示的开始和结束时间文本
    private void updateDate() {
        //1,定义标准时间的 月份+日期
        String start_date = DateUtil.INSTANCE.getMonthText(calendar_start.get(Calendar.MONTH)) + " " + calendar_start.get(Calendar.DAY_OF_MONTH) + "  ";
        String end_date = DateUtil.INSTANCE.getMonthText(calendar_end.get(Calendar.MONTH)) + " " + calendar_end.get(Calendar.DAY_OF_MONTH) + "  ";
        if (!isAllDay) {
            //2,格式化时间 HH:mm
               //2.1 开始时间的时分补零
            if (calendar_start.get(Calendar.HOUR_OF_DAY) < 10) {
                start_date += "0" + calendar_start.get(Calendar.HOUR_OF_DAY) + ":";
            } else {
                start_date += calendar_start.get(Calendar.HOUR_OF_DAY) + ":";
            }
            if (calendar_start.get(Calendar.MINUTE) < 10) {
                start_date += "0" + calendar_start.get(Calendar.MINUTE);
            } else {
                start_date += calendar_start.get(Calendar.MINUTE);
            }
            //2.2 结束时间的时分补零
            if (calendar_end.get(Calendar.HOUR_OF_DAY) < 10) {
                end_date = "0" + calendar_end.get(Calendar.HOUR_OF_DAY) + ":";
            } else {
                end_date = calendar_end.get(Calendar.HOUR_OF_DAY) + ":";
            }
            if (calendar_end.get(Calendar.MINUTE) < 10) {
                end_date += "0" + calendar_end.get(Calendar.MINUTE);
            } else {
                end_date += calendar_end.get(Calendar.MINUTE);
            }
        }
        
        //3,跨天/月/年显示完整日期
        if (calendar_start.get(Calendar.DAY_OF_MONTH) != calendar_end.get(Calendar.DAY_OF_MONTH) ||
                calendar_start.get(Calendar.MONTH) != calendar_end.get(Calendar.MONTH) ||
                calendar_start.get(Calendar.YEAR) != calendar_end.get(Calendar.YEAR)) {
            if (!isAllDay)
                end_date = DateUtil.INSTANCE.getMonthText(calendar_end.get(Calendar.MONTH)) + " " + calendar_end.get(Calendar.DAY_OF_MONTH) + "  " + end_date;
        }
        
        //4,处理年份显示
        boolean hasShowStartYear = false;
        boolean hasShowEndYear = false;
        //4.1 开始年份不等于当前年份
        if (calendar_start.get(Calendar.YEAR) != calendar_temp.get(Calendar.YEAR)) {
            hasShowStartYear = true;
            start_date = calendar_start.get(Calendar.YEAR) + " " + start_date;
        }
        //4.2 结束年份不等于当前年份
        if (calendar_end.get(Calendar.YEAR) != calendar_temp.get(Calendar.YEAR)) {
            hasShowEndYear = true;
            end_date = calendar_end.get(Calendar.YEAR) + " " + end_date;
        }
        //4.2 开始年份不等于结束年份
        if (calendar_end.get(Calendar.YEAR) != calendar_start.get(Calendar.YEAR)) {
            if (!hasShowStartYear) start_date = calendar_start.get(Calendar.YEAR) + " " + start_date;
            if (!hasShowEndYear) end_date = calendar_end.get(Calendar.YEAR) + " " + end_date;
        }
        tv_start_date.setText(start_date);
        tv_end_date.setText(end_date);
    }

    //埋点统计输入类型偏好
    private void submitEventTracking() {
        if (et_title.getText().toString().length() > 0) AnalyticsHelper.logCalendarFreeResult("title_filled");
        if (et_location.getText().toString().length() > 0) AnalyticsHelper.logCalendarFreeResult("location_filled");
        if (et_description.getText().toString().length() > 0) AnalyticsHelper.logCalendarFreeResult("description_filled");
        AnalyticsHelper.logCreateResultNumber("calendar");
    }
    //跳转到指定界面
    public static void showMe(Context context) {
        Intent intent = new Intent(context, CalenderInputActivity.class);
        context.startActivity(intent);
    }
}
