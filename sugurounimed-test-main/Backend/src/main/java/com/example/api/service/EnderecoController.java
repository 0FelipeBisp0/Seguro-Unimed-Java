package com.example.api.service;

import com.example.api.model.Endereco;
import com.example.api.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class EnderecoController {

    @Autowired
    private EnderecoRepository repository;

    public EnderecoController(EnderecoRepository repository) {
        this.repository = repository;
    }

    public List<Endereco> buscarTodosEnderecos() {
        return repository.findAll();
    }
}
