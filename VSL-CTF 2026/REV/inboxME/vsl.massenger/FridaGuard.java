package com.vsl.massenger;

import android.os.Process;
import android.util.Log;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import kotlin.Metadata;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.text.StringsKt;

/* compiled from: FridaGuard.kt */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0006\u001a\u00020\u0007J\b\u0010\b\u001a\u00020\u0007H\u0002J\b\u0010\t\u001a\u00020\u0007H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"Lcom/vsl/massenger/FridaGuard;", "", "<init>", "()V", "TAG", "", "detected", "", "hasFridaServerPort", "hasFridaLibs", "app_release"}, k = 1, mv = {2, 0, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class FridaGuard {
    public static final FridaGuard INSTANCE = new FridaGuard();
    private static final String TAG = "FridaGuard";

    private FridaGuard() {
    }

    public final boolean detected() {
        return hasFridaServerPort() || hasFridaLibs();
    }

    private final boolean hasFridaServerPort() {
        try {
            Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress("127.0.0.1", 27042), ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                CloseableKt.closeFinally(socket, null);
                return true;
            } finally {
            }
        } catch (Exception unused) {
            return false;
        }
    }

    private final boolean hasFridaLibs() {
        try {
            boolean z = true;
            String text$default = FilesKt.readText$default(new File("/proc/" + Process.myPid() + "/maps"), null, 1, null);
            if (!StringsKt.contains((CharSequence) text$default, (CharSequence) "frida", true) && !StringsKt.contains$default((CharSequence) text$default, (CharSequence) "gum-js-loop", false, 2, (Object) null)) {
                z = false;
            }
            if (z) {
                Log.w(TAG, "Frida signature found in maps");
            }
            return z;
        } catch (Exception unused) {
            return false;
        }
    }
}