package com.xiaomi.securityfirewall;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.tasks.OnSuccessListener;

public class MyOnSuccessListenerVideos implements OnSuccessListener {
    String value;
    Context contexto;
    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setContexto(Context contexto) {
        this.contexto = contexto;
    }

    public Context getContexto() {
        return contexto;
    }

    @Override
    public void onSuccess(Object o) {
        Intent i = new Intent(getContexto(), VisorVideos.class);
        i.putExtra("key", getValue());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContexto().startActivity(i);
    }
}
