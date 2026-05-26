package senac.tsi.physique.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import senac.tsi.physique.dto.UsuarioRequest;
import senac.tsi.physique.entities.Usuario;
import senac.tsi.physique.exceptions.UsuarioNotFoundException;
import senac.tsi.physique.repositories.UsuarioRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Validated
@RestController
@Tag(name = "usuarios", description = "Cadastro e consulta de usuários/alunos. Usuários são referenciados no registro de resultados de treino.")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PagedResourcesAssembler<Usuario> pagedResourcesAssembler;

    @Autowired
    public UsuarioController(UsuarioRepository usuarioRepository,
                             PagedResourcesAssembler<Usuario> pagedResourcesAssembler) {
        this.usuarioRepository = usuarioRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Listar usuários", description = "Retorna usuários paginados. Use o filtro `objetivo` para busca parcial.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/usuarios")
    public ResponseEntity<PagedModel<EntityModel<Usuario>>> getUsuarios(
            @Parameter(description = "Filtro opcional pelo objetivo do usuário", example = "Hipertrofia")
            @RequestParam(required = false) String objetivo,
            @ParameterObject Pageable pageable) {

        var usuarios = objetivo == null || objetivo.isBlank()
                ? usuarioRepository.findAll(pageable)
                : usuarioRepository.findByObjetivoContainingIgnoreCase(objetivo, pageable);

        var pagedModel = pagedResourcesAssembler.toModel(usuarios, this::toModel);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Buscar usuário por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/usuarios/{id}")
    public EntityModel<Usuario> getUsuarioById(
            @Parameter(description = "ID do usuário", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        return toModel(usuario);
    }

    @Operation(summary = "Criar usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado"),
            @ApiResponse(responseCode = "400", description = "Payload inválido")
    })
    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> createUsuario(@Valid @RequestBody UsuarioRequest request) {
        Usuario usuario = new Usuario();
        applyRequest(usuario, request);

        usuarioRepository.save(usuario);
        return ResponseEntity.created(URI.create("/usuarios/" + usuario.getId()))
                .body(usuario);
    }

    @Operation(summary = "Atualizar usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> updateUsuario(
            @Parameter(description = "ID do usuário", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id,
            @Valid @RequestBody UsuarioRequest request) {

        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        applyRequest(usuario, request);
        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }

    @Operation(summary = "Excluir usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário removido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Usuário ainda está vinculado a outros registros")
    })
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> deleteUsuario(
            @Parameter(description = "ID do usuário", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        usuarioRepository.delete(usuario);
        return ResponseEntity.noContent().build();
    }

    private void applyRequest(Usuario usuario, UsuarioRequest request) {
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(request.getSenha());
        usuario.setIdade(request.getIdade());
        usuario.setObjetivo(request.getObjetivo());
        usuario.setPesoCorporal(request.getPesoCorporal());
    }

    private EntityModel<Usuario> toModel(Usuario usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).getUsuarioById(usuario.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).getUsuarios(null, Pageable.unpaged())).withRel("usuarios"));
    }
}
