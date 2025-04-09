import az.inci.linkgenerator.factory.LinkGeneratorFactory;
import az.inci.linkgenerator.service.*;
import az.inci.linkgenerator.util.Logger;
import az.inci.linkgenerator.util.UIInteraction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.function.Supplier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LinkGeneratorTest {

    @Test
    void testGenerateForSelected_whenNoMatches_logWarning() {
        Logger logger = mock(Logger.class);
        UIInteraction uiInteraction = mock(UIInteraction.class);
        FileService fileService = mock(FileService.class);
        FtpService ftpService = mock(FtpService.class);
        LinkGenerator linkGenerator = mock(LinkGenerator.class);
        Supplier<String> supplier = () -> "a.";
        LinkGeneratorFactory factory = () -> linkGenerator;
        CodeListProcessor codeListProcessor = new CodeListProcessor(uiInteraction, logger, supplier, factory);
        codeListProcessor.handleGenerateFromListClick();

        verify(logger).logWarning("Daxil edilən kodlara uyğun mal tapılmadı.");
        verifyNoInteractions(fileService, ftpService);
    }

    @Test
    void testWhenEmptyList_logWarning() {
        Logger logger = mock(Logger.class);
        UIInteraction uiInteraction = mock(UIInteraction.class);
        LinkGenerator linkGenerator = mock(LinkGenerator.class);
        Supplier<String> supplier = () -> "";
        LinkGeneratorFactory factory = () -> linkGenerator;
        CodeListProcessor codeListProcessor = new CodeListProcessor(uiInteraction, logger, supplier, factory);
        codeListProcessor.handleGenerateFromListClick();
        verify(logger).logWarning("Kod siyahısı boşdur. Ən azı 1 kod əlavə edin.");
        verify(uiInteraction).focusOnInvCodeList();
        verify(uiInteraction).disableControls(false);
    }

    @Test
    void testOnValidInput() throws InterruptedException {
        Logger logger = mock(Logger.class);
        UIInteraction uiInteraction = mock(UIInteraction.class);
        Supplier<String> supplier = () -> "a000001 x002";
        LinkGenerator linkGenerator = mock(LinkGenerator.class);
        LinkGeneratorFactory factory = () -> linkGenerator;
        CodeListProcessor codeListProcessor = new CodeListProcessor(uiInteraction, logger, supplier, factory);
        codeListProcessor.handleGenerateFromListClick();

        Thread.sleep(100);

        verify(linkGenerator).generateForSelected(Arrays.asList("a000001", "x002"));
        verify(uiInteraction).disableControls(true);
        verify(uiInteraction).disableControls(false);
    }
}
