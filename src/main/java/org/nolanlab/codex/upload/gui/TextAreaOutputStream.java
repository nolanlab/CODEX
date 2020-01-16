package org.nolanlab.codex.upload.gui;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author Vishal
 */
public class TextAreaOutputStream extends OutputStream {

    private final JTextArea textArea;
    private final StringBuilder sb = new StringBuilder();
    private String title;
    private final File location;

    public TextAreaOutputStream(final JTextArea textArea, String title, final File location) {
        this.textArea = textArea;
        this.title = title;
        this.location = location;
        sb.append(title + "> ");
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    @Override
    public void write(int b) throws IOException {

        if (b == '\r')
            return;

        if (b == '\n') {
            final String text = sb.toString() + "\n";
            SwingUtilities.invokeLater(() -> {
                textArea.append(text);
                if(location != null) {
                    try {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(location, true));
                        bufferedWriter.write(dateFormat.format(date) + "\t" + text);
                        bufferedWriter.flush();
                        bufferedWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            sb.setLength(0);
            sb.append(title + "> ");
            return;
        }
        sb.append((char) b);
    }
}