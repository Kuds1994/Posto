package com.kudu.posto.utils;

import android.content.Context;
import android.icu.util.Currency;
import android.os.Build;

import androidx.annotation.RequiresApi;

import me.abhinay.input.CurrencyEditText;


public class InputCurrencyBrasil extends CurrencyEditText {

    public InputCurrencyBrasil(Context context) {
        super(context);
    }

    @Override
    public void setCurrency(String currencySymbol) {
        super.setCurrency("R$");
    }

    @Override
    public void setDelimiter(boolean value) {
        super.setDelimiter(false);
    }

    @Override
    public void setSpacing(boolean value) {
        super.setSpacing(false);
    }

    @Override
    public void setDecimals(boolean value) {
        super.setDecimals(true);
    }

    @Override
    public void setSeparator(String value) {
        super.setSeparator(".");
    }
}
