package Application.config;

import Application.domain.ports.in.AlchemyTrackingUseCase;
import Application.domain.ports.out.DeviceInfoRepositoryPort;
import Application.domain.services.AlchemyTrackingService;
import Application.domain.services.MessageReconstructionService;
import Application.domain.services.TrilaterationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// las clases del dominio (Application.domain.*) son POJOs puros, sin
// anotaciones de Spring, para que el dominio no dependa de ningun
// framework. aqui, en la capa de configuracion, es donde se conectan
// como beans de Spring - el mismo patron que DomainServicesConfig en NexusM.
@Configuration
public class DomainServicesConfig {

    @Bean
    public TrilaterationService trilaterationService() {
        return new TrilaterationService();
    }

    @Bean
    public MessageReconstructionService messageReconstructionService() {
        return new MessageReconstructionService();
    }

    @Bean
    public AlchemyTrackingUseCase alchemyTrackingUseCase(DeviceInfoRepositoryPort repository,
                                                           TrilaterationService trilaterationService,
                                                           MessageReconstructionService messageReconstructionService) {
        return new AlchemyTrackingService(repository, trilaterationService, messageReconstructionService);
    }
}
