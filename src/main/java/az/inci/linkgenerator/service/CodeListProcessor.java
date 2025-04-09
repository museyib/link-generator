package az.inci.linkgenerator.service;

import az.inci.linkgenerator.factory.LinkGeneratorFactory;
import az.inci.linkgenerator.util.Logger;
import az.inci.linkgenerator.util.UIInteraction;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class CodeListProcessor {
    private final UIInteraction uiInteraction;
    private final Logger logger;
    private final Supplier<String> codeListSupplier;
    private final LinkGeneratorFactory linkGeneratorFactory;

    public CodeListProcessor(UIInteraction uiInteraction,
                             Logger logger,
                             Supplier<String> codeListSupplier,
                             LinkGeneratorFactory linkGeneratorFactory) {
        this.uiInteraction = uiInteraction;
        this.logger = logger;
        this.codeListSupplier = codeListSupplier;
        this.linkGeneratorFactory = linkGeneratorFactory;
    }

    public void handleGenerateFromListClick() {
        uiInteraction.disableControls(true);

        List<String> codeList = Arrays.stream(
                codeListSupplier.get()
                        .replaceAll("\n", " ")
                        .toLowerCase()
                        .split(" "))
                .filter(s -> !s.isEmpty()).toList();
        if (codeList.isEmpty()) {
            logger.logWarning("Kod siyahısı boşdur. Ən azı 1 kod əlavə edin.");
            uiInteraction.focusOnInvCodeList();
            uiInteraction.disableControls(false);
        }
        else {
            new Thread(() -> {
                LinkGenerator linkGenerator = linkGeneratorFactory.create();
                linkGenerator.generateForSelected(codeList);
                uiInteraction.disableControls(false);
            }).start();
        }
    }
}
