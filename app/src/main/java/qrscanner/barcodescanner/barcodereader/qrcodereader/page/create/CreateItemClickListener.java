package qrscanner.barcodescanner.barcodereader.qrcodereader.page.create;

import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.result.CreateType;


//创建页列表项点击事件监听接口
public interface CreateItemClickListener {
    //当用户点击某个创建类型时触发
    void onCreateItemClick(int position, CreateType type);
}
