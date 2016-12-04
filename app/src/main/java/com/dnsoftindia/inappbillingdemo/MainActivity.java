package com.dnsoftindia.inappbillingdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dnsoftindia.inappbillingdemo.util.IabHelper;
import com.dnsoftindia.inappbillingdemo.util.IabResult;
import com.dnsoftindia.inappbillingdemo.util.Inventory;
import com.dnsoftindia.inappbillingdemo.util.Purchase;

public class MainActivity extends AppCompatActivity {

    private static final String tag = MainActivity.class.getSimpleName();

    IabHelper mHelper;
//    static final String ITEM_SKU="android.test.purchased";
    static final String ITEM_SKU="com.dnsoftindia.inappbillingdemo.buttonclick";

    Button btnBuyAClick;
    Button btnClickMe;

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                @Override
                public void onConsumeFinished(Purchase purchase, IabResult result) {
                    if (result.isSuccess()) {
                        btnClickMe.setEnabled(true);
                    }
                    else {
                        Log.d(tag, "Consuming purchase failed"+result.getMessage());
                    }
                }
            };

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener =
            new IabHelper.QueryInventoryFinishedListener() {
                @Override
                public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                    if (result.isFailure()) {
                        Log.d(tag, "Querying purchase failed"+result.getMessage());
                    }
                    else {
                        mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU), mConsumeFinishedListener);
                    }
                }
            };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener =
            new IabHelper.OnIabPurchaseFinishedListener() {
                @Override
                public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                    if (result.isFailure()) {
                        Log.d(tag, "Purchase click failed: "+result.getMessage());
                        return;
                    }
                    else {
                        if (purchase.getSku().equalsIgnoreCase(ITEM_SKU)) {
                            consumeItem();
                            btnClickMe.setEnabled(false);
                        }
                    }
                }
    };

    public void consumeItem() {
         mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBuyAClick = (Button) findViewById(R.id.btnBuyAClick);
        btnClickMe = (Button) findViewById(R.id.btnClickMe);
        btnClickMe.setEnabled(false);

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlVBpLugzzkXcm" +
                "N6fGPgPE/fZ3JEIZqRt1qlGzwd2u+mJ9r8t+TRHjHQDA1LoVDzNZV5FW8xFciAATynyrGC7beQsLFR5hv" +
                "x77hH+cOoE6Ty4d36xZvzppe4ADp8dEgigw3Ch6I6F6+afL1sLs21IjC8tZId6ZAAs84l/yzO9K+K8NCo" +
                "xP9REnQXKMCxGIwXetWIDz+FDqeAgS1q6DKcklCGSTRgtKOdfQrlVEyVP9d56NPE+gc6Nsy0pMsBM2fOO" +
                "S67+fQsh5JxaiZWxS1B/8/vFCynwNcx6OFb2xOTseG9g149wX6gdVqubjC6UpoQm4sXu9UWMjTcCFgawJ" +
                "9usBwIDAQAB";

        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(tag, "In-app Billing setup failed: " + result);
                }
                else {
                    Log.d(tag, "In-app Billing is set up OK");
                }
            }
        });
    }

    public void buyClick(View v) {
        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001, mPurchaseFinishedListener, "mypurchasetoken");
    }

    public void buttonClick(View v) {
        btnClickMe.setEnabled(false);
        btnBuyAClick.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            mHelper.dispose();
        }
        mHelper = null;
    }
}
