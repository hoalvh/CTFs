package com.vsl.flagchecker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.textfield.TextInputEditText;
import kotlin.Metadata;

/* compiled from: PinActivity.kt */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \f2\u00020\u0001:\u0001\fB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0011\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0082 J\u0012\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0014¨\u0006\r"}, d2 = {"Lcom/vsl/flagchecker/PinActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "checkPin", "", "input", "", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "Companion", "app_release"}, k = 1, mv = {2, 0, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class PinActivity extends AppCompatActivity {
    private final native boolean checkPin(String input);

    static {
        System.loadLibrary("veilcore");
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        final TextInputEditText textInputEditText = (TextInputEditText) findViewById(R.id.pin_input);
        ((Button) findViewById(R.id.pin_button)).setOnClickListener(new View.OnClickListener() { // from class: com.vsl.flagchecker.PinActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PinActivity.onCreate$lambda$0(textInputEditText, this, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$0(TextInputEditText textInputEditText, PinActivity pinActivity, View view) {
        String string;
        Editable text = textInputEditText.getText();
        if (text == null || (string = text.toString()) == null) {
            string = "";
        }
        if (pinActivity.checkPin(string)) {
            pinActivity.startActivity(new Intent(pinActivity, (Class<?>) MainActivity.class));
            pinActivity.finish();
        } else {
            Toast.makeText(pinActivity, "Invalid PIN", 0).show();
        }
    }
}