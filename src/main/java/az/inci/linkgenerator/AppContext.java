package az.inci.linkgenerator;

import az.inci.linkgenerator.factory.LinkGeneratorFactory;
import az.inci.linkgenerator.service.*;
import az.inci.linkgenerator.util.Logger;
import az.inci.linkgenerator.util.UIInteraction;

import java.util.function.Supplier;

public record AppContext(UIInteraction ui, Logger logger) {

    public InventoryService getInventoryService() {
        return new InventoryServiceImpl(logger);
    }

    public FileService getFileService() {
        return new FileServiceImpl(logger);
    }

    public FtpService getFtpService() {
        return new FtpServiceImpl(logger);
    }

    public LinkGeneratorFactory getLinkGeneratorFactory() {
        return () -> new LinkGenerator(
                ui,
                logger,
                getInventoryService(),
                getFileService(),
                getFtpService()
        );
    }

    public CodeListProcessor getCodeListProcessor(Supplier<String> codeSupplier) {
        return new CodeListProcessor(
                ui,
                logger,
                codeSupplier,
                getLinkGeneratorFactory()
        );
    }
}
