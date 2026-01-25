package com.vsl.massenger;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;

/* compiled from: NativeProtocol.kt */
@Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u0012\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\t\u0010\u0004\u001a\u00020\u0005H\u0086 J\t\u0010\u0006\u001a\u00020\u0007H\u0086 J)\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u000eH\u0086 J!\u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\t2\u0006\u0010\u0011\u001a\u00020\u00072\u0006\u0010\u0012\u001a\u00020\u000eH\u0086 J\u0013\u0010\u0013\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u0010\u001a\u00020\tH\u0086 J\u0011\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0010\u001a\u00020\tH\u0086 ¨\u0006\u0016"}, d2 = {"Lcom/vsl/massenger/NativeProtocol;", "", "<init>", "()V", "getHostFromNative", "", "getPortFromNative", "", "connectAndHandshake", "", "host", "port", "pinHex", "pinnedCert", "", "sendCommand", "handle", "command", "data", "recvInner", "close", "", "app_release"}, k = 1, mv = {2, 0, 0}, xi = ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE)
/* loaded from: classes.dex */
public final class NativeProtocol {
    public static final NativeProtocol INSTANCE = new NativeProtocol();

    public final native void close(long handle);

    public final native long connectAndHandshake(String host, int port, String pinHex, byte[] pinnedCert);

    public final native String getHostFromNative();

    public final native int getPortFromNative();

    public final native byte[] recvInner(long handle);

    public final native int sendCommand(long handle, int command, byte[] data);

    private NativeProtocol() {
    }

    static {
        System.loadLibrary("hostprovider");
        System.loadLibrary("handshakecrypto");
    }
}