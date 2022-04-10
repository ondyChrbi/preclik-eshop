package cz.preclik.shop.preclikshop.assembler;

import cz.preclik.shop.preclikshop.controller.ProductControllerV1;
import cz.preclik.shop.preclikshop.domain.Product;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductModelAssemblerV1 implements RepresentationModelAssembler<Product, EntityModel<Product>> {
    @Override
    public EntityModel<Product> toModel(Product inventory) {

        return EntityModel.of(inventory,
                linkTo(methodOn(ProductControllerV1.class).findById(inventory.getId()))
                        .withSelfRel());
    }
}
