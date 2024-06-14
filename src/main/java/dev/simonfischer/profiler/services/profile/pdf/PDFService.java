package dev.simonfischer.profiler.services.profile.pdf;

public interface PDFService {
    /**
     * Converts an HTML string into a PDF byte array.
     *
     * @param html The HTML string to convert.
     * @return The PDF byte array representing the converted HTML.
     */
    byte[] getBytePdf(String html);
}
