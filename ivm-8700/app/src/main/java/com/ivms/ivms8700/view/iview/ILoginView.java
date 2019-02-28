package com.ivms.ivms8700.view.iview;

public interface ILoginView {

    public void showLoginProgress();


    public void cancelProgress();

    public void onLoginFailed();

    public void onLoginSuccess();

}
