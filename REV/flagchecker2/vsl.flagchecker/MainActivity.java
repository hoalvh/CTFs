package com.vsl.flagchecker;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import kotlin.Metadata;

/* compiled from: MainActivity.kt */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u000b2\u00020\u0001:\u0001\u000bB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0011\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u0082 J\u0012\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0014¨\u0006\f"}, d2 = {"Lcom/vsl/flagchecker/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "validateFlag", "", "input", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "Companion", "app_release"}, k = 1, mv = {2, 0, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class MainActivity extends AppCompatActivity {
    private final native String validateFlag(String input);

    static {
        System.loadLibrary("veilcore");
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextInputEditText textInputEditText = (TextInputEditText) findViewById(R.id.flag_input);
        MaterialButton materialButton = (MaterialButton) findViewById(R.id.check_button);
        final MaterialTextView materialTextView = (MaterialTextView) findViewById(R.id.output_text);
        materialButton.setOnClickListener(new View.OnClickListener() { // from class: com.vsl.flagchecker.MainActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.onCreate$lambda$0(textInputEditText, this, materialTextView, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$0(TextInputEditText textInputEditText, MainActivity mainActivity, MaterialTextView materialTextView, View view) {
        Editable text = textInputEditText.getText();
        String string = text != null ? text.toString() : null;
        if (string == null) {
            string = "";
        }
        materialTextView.setText(mainActivity.validateFlag(string));
    }
}