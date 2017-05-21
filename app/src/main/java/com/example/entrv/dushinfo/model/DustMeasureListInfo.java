
package com.example.entrv.dushinfo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DustMeasureListInfo {

    @SerializedName("MsrstnInfoInqireSvrVo")
    @Expose
    private MsrstnInfoInqireSvrVo msrstnInfoInqireSvrVo;
    @SerializedName("list")
    @Expose
    private java.util.List<com.example.entrv.dushinfo.model.List> list = null;
    @SerializedName("parm")
    @Expose
    private Parm parm;
    @SerializedName("totalCount")
    @Expose
    private int totalCount;

    public MsrstnInfoInqireSvrVo getMsrstnInfoInqireSvrVo() {
        return msrstnInfoInqireSvrVo;
    }

    public void setMsrstnInfoInqireSvrVo(MsrstnInfoInqireSvrVo msrstnInfoInqireSvrVo) {
        this.msrstnInfoInqireSvrVo = msrstnInfoInqireSvrVo;
    }

    public java.util.List<com.example.entrv.dushinfo.model.List> getList() {
        return list;
    }

    public void setList(java.util.List<com.example.entrv.dushinfo.model.List> list) {
        this.list = list;
    }

    public Parm getParm() {
        return parm;
    }

    public void setParm(Parm parm) {
        this.parm = parm;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

}
