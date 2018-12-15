package cn.bluemobi.dylan.step.bmobJava;

import cn.bmob.v3.BmobUser;

public class MyUser extends BmobUser {
    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    private Integer step;
    private Integer ranking;
}
