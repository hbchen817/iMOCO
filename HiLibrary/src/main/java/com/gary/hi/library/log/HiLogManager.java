package com.gary.hi.library.log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HiLogManager {

    private HiLogConfig mConfig;
    private static HiLogManager mInstance;
    private List<HiLogPrinter> mPrinters = new ArrayList<>();

    private HiLogManager(HiLogConfig config, HiLogPrinter[] printers) {
        mConfig = config;
        mPrinters.addAll(Arrays.asList(printers));
    }

    public static HiLogManager getInstance() {
        return mInstance;
    }

    public static void init(@NotNull HiLogConfig config, HiLogPrinter... printers) {
        mInstance = new HiLogManager(config, printers);
    }

    public HiLogConfig getHiLogConfig() {
        return mConfig;
    }

    public List<HiLogPrinter> getPrinters() {
        return mPrinters;
    }

    public void addPrinter(HiLogPrinter printer) {
        mPrinters.add(printer);
    }

    public void removePrinter(HiLogPrinter printer) {
        if (mPrinters != null) {
            getPrinters().remove(printer);
        }
    }

}
