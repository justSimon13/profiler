package dev.simonfischer.profiler.services.profile.pdf;

import org.springframework.stereotype.Service;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

@Service
public class PDFServiceImpl implements PDFService {
    public byte[] getBytePdf(String html) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            ITextRenderer renderer = new ITextRenderer();
            SharedContext sharedContext = renderer.getSharedContext();
            sharedContext.setPrint(true);
            sharedContext.setInteractive(false);
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(byteArrayOutputStream);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return byteArrayOutputStream.toByteArray();
    }

}
