package br.com.tenda.coupon.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Coupon Management API")
                        .version("1.0.0")
                        .description("API para gerenciamento de cupons de desconto com foco em Clean Architecture e boas práticas"));
    }

    @Bean
    public OperationCustomizer customizePageable() {
        return (operation, handlerMethod) -> {
            if (operation.getParameters() != null) {
                List<Parameter> parameters = operation.getParameters().stream()
                        .filter(param -> !"sort".equals(param.getName()))
                        .collect(Collectors.toList());

                boolean hasPageable = handlerMethod.getMethodParameters().length > 0 &&
                        java.util.Arrays.stream(handlerMethod.getMethodParameters())
                                .anyMatch(param -> Pageable.class.isAssignableFrom(param.getParameterType()));

                if (hasPageable) {
                    parameters = parameters.stream()
                            .filter(param -> !"page".equals(param.getName()) &&
                                           !"size".equals(param.getName()) &&
                                           !"sort".equals(param.getName()))
                            .collect(Collectors.toList());

                    Parameter sizeParam = new Parameter()
                            .in("query")
                            .name("size")
                            .description("Quantidade de itens por página")
                            .required(false)
                            .schema(new IntegerSchema()._default(20));

                    parameters.add(sizeParam);
                }

                operation.setParameters(parameters);
            }
            return operation;
        };
    }
}
