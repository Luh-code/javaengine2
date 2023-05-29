package org.app.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;

//! QUICK AND DIRTY

public class CallbackPrintStream extends PrintStream {
    private Consumer<String> logFunc = Logger::logError;

    @SuppressWarnings("unused")
    public CallbackPrintStream(OutputStream out) {
        super(out);
    }

    public CallbackPrintStream(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    @SuppressWarnings("unused")
    public CallbackPrintStream(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
        super(out, autoFlush, encoding);
    }

    @SuppressWarnings("unused")
    public CallbackPrintStream(OutputStream out, boolean autoFlush, Charset charset) {
        super(out, autoFlush, charset);
    }

    @SuppressWarnings("unused")
    public CallbackPrintStream(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    @SuppressWarnings("unused")
    public CallbackPrintStream(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    @SuppressWarnings("unused")
    public CallbackPrintStream(String fileName, Charset charset) throws IOException {
        super(fileName, charset);
    }

    @SuppressWarnings("unused")
    public CallbackPrintStream(File file) throws FileNotFoundException {
        super(file);
    }

    @SuppressWarnings("unused")
    public CallbackPrintStream(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    @SuppressWarnings("unused")
    public CallbackPrintStream(File file, Charset charset) throws IOException {
        super(file, charset);
    }

    public void setLogFunc(Consumer<String> logFunc) {
        this.logFunc = logFunc;
    }

    @Override
    public void flush() {
        Logger.printStream.flush();
    }

    @Override
    public void close() {
        Logger.printStream.close();
    }

    @Override
    public boolean checkError() {
        return Logger.printStream.checkError();
    }

    @Override
    protected void setError() {
        Logger.printStream.setError();
    }

    @Override
    protected void clearError() {
        Logger.printStream.clearError();
    }

    @Override
    public void write(int b) {
        Logger.printStream.write(b);
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        Logger.printStream.write(buf, off, len);
    }

    @Override
    public void write(byte[] buf) throws IOException {
        Logger.printStream.write(buf);
    }

    @Override
    public void writeBytes(byte[] buf) {
        Logger.printStream.writeBytes(buf);
    }

    @Override
    public void print(boolean b) {
        logFunc.accept(String.valueOf(b));
    }

    @Override
    public void print(char c) {
        logFunc.accept(String.valueOf(c));
    }

    @Override
    public void print(int i) {
        logFunc.accept(String.valueOf(i));
    }

    @Override
    public void print(long l) {
        logFunc.accept(String.valueOf(l));
    }

    @Override
    public void print(float f) {
        logFunc.accept(String.valueOf(f));
    }

    @Override
    public void print(double d) {
        logFunc.accept(String.valueOf(d));
    }

    @Override
    public void print(char[] s) {
        logFunc.accept(Arrays.toString(s));
    }

    @Override
    public void print(String s) {
        logFunc.accept(s);
    }

    @Override
    public void print(Object obj) {
        logFunc.accept(String.valueOf(obj));
    }

    @Override
    public void println() {
        Logger.logEmpty();
    }

    @Override
    public void println(boolean x) {
        logFunc.accept(String.valueOf(x));
    }

    @Override
    public void println(char x) {
        logFunc.accept(String.valueOf(x));
    }

    @Override
    public void println(int x) {
        logFunc.accept(String.valueOf(x));
    }

    @Override
    public void println(long x) {
        logFunc.accept(String.valueOf(x));
    }

    @Override
    public void println(float x) {
        logFunc.accept(String.valueOf(x));
    }

    @Override
    public void println(double x) {
        logFunc.accept(String.valueOf(x));
    }

    @Override
    public void println(char[] x) {
        logFunc.accept(String.valueOf(x));
    }

    @Override
    public void println(String x) {
        logFunc.accept(x);
    }

    @Override
    public void println(Object x) {
        logFunc.accept(String.valueOf(x));
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        logFunc.accept(String.format(format, args));
        return this;
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        logFunc.accept(String.format(format, args));
        return this;
    }

    @Override
    public PrintStream format(String format, Object... args) {
        logFunc.accept(String.format(format, args));
        return this;
    }

    @Override
    public PrintStream format(Locale l, String format, Object... args) {
        logFunc.accept(String.format(format, args));
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq) {
        Logger.printStream.append(csq);
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        Logger.printStream.append(csq, start, end);
        return this;
    }

    @Override
    public PrintStream append(char c) {
        Logger.printStream.append(c);
        return this;
    }

    @Override
    public int hashCode() {
        return Logger.printStream.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return Logger.printStream.toString();
    }
}
