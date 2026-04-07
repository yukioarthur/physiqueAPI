package Physique.api.assemblers;

import Physique.api.controllers.UsuarioController;
import Physique.api.entities.Usuario;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(UsuarioController.class).buscarPorId(entity.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).listar(org.springframework.data.domain.Pageable.unpaged())).withRel("collection"),
                linkTo(methodOn(UsuarioController.class).deletar(entity.getId())).withRel("delete")
        );
    }
}
