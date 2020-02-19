package ir.carpino.tracker.utils;

import ir.carpino.tracker.entity.CsvLogData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class CsvLogger<T extends CsvLogData> {
    private final BufferedWriter bufferedWriter;
    private final CSVPrinter csvPrinter;
    private final PrintWriter printWriter;

    public CsvLogger(File file) throws IOException {
        printWriter = new PrintWriter(file);
        bufferedWriter = new BufferedWriter(this.printWriter);
        csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.RFC4180);
    }

    public void log(T data) throws IOException {
        csvPrinter.printRecord(data.getValues());
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        csvPrinter.close();
        bufferedWriter.close();
        printWriter.close();
    }

    public void flush() throws IOException {
        csvPrinter.flush();
    }
}
