package com.webview.perf;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public class WebViewPerformanceVisitor extends ClassVisitor {

    public WebViewPerformanceVisitor(ClassVisitor cv) {
        super(Opcodes.ASM9, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                    String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        // 监控关键的WebView方法
        if (isLoadUrlMethod(name, descriptor) ||
            isOnPageStartedMethod(name, descriptor) ||
            isOnPageFinishedMethod(name, descriptor) ||
            isOnRenderProcessGoneMethod(name, descriptor)) {

            return new WebViewMethodVisitor(api, mv, access, name, descriptor);
        }

        return mv;
    }

    private boolean isLoadUrlMethod(String name, String desc) {
        return name.equals("loadUrl") || name.equals("loadData") || name.equals("loadDataWithBaseURL");
    }

    private boolean isOnPageStartedMethod(String name, String desc) {
        return name.equals("onPageStarted");
    }

    private boolean isOnPageFinishedMethod(String name, String desc) {
        return name.equals("onPageFinished");
    }

    private boolean isOnRenderProcessGoneMethod(String name, String desc) {
        return name.equals("onRenderProcessGone");
    }

    private static class WebViewMethodVisitor extends AdviceAdapter {

        private final String methodName;

        protected WebViewMethodVisitor(int api, MethodVisitor mv, int access,
                                     String name, String desc) {
            super(api, mv, access, name, desc);
            this.methodName = name;
        }

        @Override
        protected void onMethodEnter() {
            // 方法开始时记录时间
            loadArg(0); // 加载第一个参数（通常是WebView实例或URL）
            invokeStatic(Type.getType("com/webview/perf/monitor/WebViewMonitor"),
                         org.objectweb.asm.commons.Method.getMethod(
                             "void onMethodEnter(java.lang.String, java.lang.Object)"));
        }

        @Override
        protected void onMethodExit(int opcode) {
            // 方法退出时记录
            loadArg(0);
            invokeStatic(Type.getType("com/webview/perf/monitor/WebViewMonitor"),
                         org.objectweb.asm.commons.Method.getMethod(
                             "void onMethodExit(java.lang.String, java.lang.Object)"));
        }
    }
}
