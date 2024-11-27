package com.programmingtech.orderservice.service;

import com.programmingtech.orderservice.dto.InventoryResponse;
import com.programmingtech.orderservice.dto.OrderLineItemsDto;
import com.programmingtech.orderservice.dto.OrderRequest;
import com.programmingtech.orderservice.model.Order;
import com.programmingtech.orderservice.model.OrderLineItems;
import com.programmingtech.orderservice.repository.OrderRepository;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClient;
    private final Tracer tracer;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCode = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode).toList();

        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");
        try(Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())) {

            InventoryResponse[] inventoryResponseArray  = webClient.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCode).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            boolean allProductsInStock = Arrays
                    .stream(inventoryResponseArray)
                    .allMatch(InventoryResponse::isInStock);

            if(allProductsInStock) {
                orderRepository.save(order);
                return "Order Placed Successfully";
            } else {
                throw new IllegalArgumentException("Product is not in stock, please try again later");
            }
        } finally {
            inventoryServiceLookup.end();
        }



    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
