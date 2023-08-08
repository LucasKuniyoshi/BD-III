package com.example.meusgastosturmab.domain.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.meusgastosturmab.domain.dto.titulo.TituloRequestDTO;
import com.example.meusgastosturmab.domain.dto.titulo.TituloResponseDTO;
import com.example.meusgastosturmab.domain.exception.ResourceNotFoundException;
import com.example.meusgastosturmab.domain.model.Titulo;
import com.example.meusgastosturmab.domain.model.Usuario;
import com.example.meusgastosturmab.domain.repository.TituloRepository;

@Service //Sem isso a service na funciona
public class TituloService implements ICRUDService<TituloRequestDTO, TituloResponseDTO>{
    @Autowired
    private TituloRepository tituloRepository;
    @Autowired
    private ModelMapper mapper;


    @Override
    public List<TituloResponseDTO> obterTodos() {
        //pega o usuario q está logado
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Titulo> titulos = tituloRepository.findByUsuario(usuario);
        return titulos.stream()
        .map(titulo -> mapper.map(titulo, TituloResponseDTO.class)).collect(Collectors.toList());
    }

    @Override
    public TituloResponseDTO obterPorId(Long id) {
        Optional<Titulo> optTitulo = tituloRepository.findById(id);

        if(optTitulo.isEmpty()){
            throw new ResourceNotFoundException("Não foi possível encontrar o titulo com o id" + id);
        }
        return mapper.map(optTitulo.get(), TituloResponseDTO.class);

    }

    @Override
    public TituloResponseDTO cadastrar(TituloRequestDTO dto) {
        validarTitulo(dto);
        Titulo titulo = mapper.map(dto, Titulo.class);
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        titulo.setUsuario(usuario);
        titulo.setId(null);// cria id nullo para criar um do zero e n atualizar um id ja existente
        titulo.setDataCadastro(new Date());
        titulo = tituloRepository.save(titulo);
        return mapper.map(titulo, TituloResponseDTO.class);
    }

    @Override
    public TituloResponseDTO atualizar(Long id, TituloRequestDTO dto) {
        TituloResponseDTO tituloBancDto = obterPorId(id);
        validarTitulo(dto);
        Titulo titulo = mapper.map(dto, Titulo.class);
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        titulo.setUsuario(usuario);
        titulo.setId(id);
        titulo.setDataCadastro(tituloBancDto.getDataCadastro());
        titulo = tituloRepository.save(titulo);
        return mapper.map(titulo, TituloResponseDTO.class);
    }

    @Override
    public void deletar(Long id) {
        obterPorId(id);
        tituloRepository.deleteById(id);
    }
    
    private void validarTitulo(TituloRequestDTO dto){
        if(dto.getTipo() == null || dto.getDataVencimento() == null || 
        dto.getValor() == null || dto.getDescricao() == null){
            throw new ResourceNotFoundException("Titulo " + " Invalido - Campos Obrigatórios");
        }
    }

    public List<TituloResponseDTO> obterPorDataVencimento(String periodoIncial, String periodoFinal){
        List<Titulo> titulos = tituloRepository
        .obterFluxoCaixaPorDataVencimento(periodoIncial, periodoFinal);
        return titulos.stream()
        .map(titulo -> mapper.map(titulo, TituloResponseDTO.class))
        .collect(Collectors.toList());
    }
}
