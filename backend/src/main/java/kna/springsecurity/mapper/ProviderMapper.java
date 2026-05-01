package kna.springsecurity.mapper;

import kna.springsecurity.dto.ProviderDTO.ProviderResponse;
import kna.springsecurity.entity.Provider;
import org.springframework.stereotype.Component;

@Component
public class ProviderMapper {

    public ProviderResponse mapToProviderResponse (Provider provider){

        if(provider == null){
            throw new IllegalStateException("The provider not found");
        }

        return ProviderResponse.builder()
                .providerId(provider.getId())
                .providerName(provider.getName())
                .build();
    }
}
