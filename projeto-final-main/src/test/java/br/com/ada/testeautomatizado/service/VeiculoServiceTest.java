package br.com.ada.testeautomatizado.service;

import br.com.ada.testeautomatizado.dto.VeiculoDTO;
import br.com.ada.testeautomatizado.exception.PlacaInvalidaException;
import br.com.ada.testeautomatizado.exception.VeiculoNaoEncontradoException;
import br.com.ada.testeautomatizado.model.Veiculo;
import br.com.ada.testeautomatizado.repository.VeiculoRepository;
import br.com.ada.testeautomatizado.util.Response;
import br.com.ada.testeautomatizado.util.ValidacaoPlaca;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @InjectMocks
    private VeiculoService service;
    @Mock
    private VeiculoRepository repository;

    @Mock
    private ValidacaoPlaca validacaoPlaca;

    @Test
    @DisplayName("Deve retornar veiculo cadastrado com sucesso.")
    public void deveCriarCadastroDeVeiculo() {
        doCallRealMethod().when(validacaoPlaca).isPlacaValida(anyString());
        when(repository.save(Mockito.any(Veiculo.class))).thenReturn(getVeiculoBD());
        Assertions.assertEquals(ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response<>("Sucesso", getVeiculoDTO())), service.cadastrar(getVeiculoDTO()));
    }

    @Test
    @DisplayName("Deve retornar erro placa invalida ao cadastrar.")
    public void deveRetonarErroPlacaInvalidaAoCadastrar() {
        VeiculoDTO veiculo = new VeiculoDTO();
        veiculo.setPlaca("ABC1234");
        doCallRealMethod().when(validacaoPlaca).isPlacaValida(anyString());
        assertEquals(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new Response<>("Placa invalida!", null)), service.cadastrar(veiculo));
    }

    @Test
    @DisplayName("Deve retornar erro veiculo nulo.")
    public void deveRetornarErroAoCadastrarVeiculoNulo() {
        doCallRealMethod().when(validacaoPlaca).isPlacaValida(anyString());
        when(repository.save(any(Veiculo.class))).thenThrow(NullPointerException.class);
        assertThrows(Exception.class, () -> service.cadastrar(getVeiculoDTO()));
    }

    @Test
    @DisplayName("Deve remover veiculo")
    public void deveRemoverVeiculo() {
        String placa = "XYZ-4578";
        doCallRealMethod().when(validacaoPlaca).isPlacaValida(anyString());
        when(repository.findByPlaca(placa)).thenReturn(Optional.of(getVeiculoBD()));
        Response<Boolean> response = new Response<>("Sucesso", Boolean.TRUE);
        Assertions.assertEquals(ResponseEntity.ok(response), service.deletarVeiculoPelaPlaca(placa));
    }

    @Test
    @DisplayName("Deve lancar erro ao remover veiculo placa invalida")
    public void deveRetornarErroAoRemoverVeiculoPlacaInvalida() {
        String placa = "XYZ4578";
        doCallRealMethod().when(validacaoPlaca).isPlacaValida(anyString());
        Response<Boolean> response = new Response<Boolean>("Placa invalida!", Boolean.FALSE);
        Assertions.assertEquals(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response), service.deletarVeiculoPelaPlaca(placa));
    }

    @Test
    @DisplayName("Deve lancar erro ao remover veiculo nao encontrado")
    public void deveRetornarErroAoRemoverVeiculoNaoEncontrado() {
        String placa = "XYZ-4578";
        doCallRealMethod().when(validacaoPlaca).isPlacaValida(anyString());
        Response<Boolean> response = new Response<Boolean>("Veículo não encontrado", Boolean.FALSE);
        Assertions.assertEquals(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response), service.deletarVeiculoPelaPlaca(placa));
    }

    @Test
    @DisplayName("Deve lancar erro ao remover veiculo nulo")
    public void deveRetornarErroAoRemoverVeiculoNulo() {
        String placa = "XYZ-4578";
        doCallRealMethod().when(validacaoPlaca).isPlacaValida(anyString());
        when(repository.findByPlaca(anyString())).thenReturn(nullable(Optional.class));
        assertThrows(Exception.class, () -> service.deletarVeiculoPelaPlaca(placa));
    }

    @Test
    @DisplayName("Deve atualizar veiculo")
    public void deveAtualizarVeiculo() {
        doCallRealMethod().when(validacaoPlaca).isPlacaValida(anyString());

        when(repository.findByPlaca(anyString())).thenReturn(Optional.of(getVeiculoBD()));

        Veiculo veiculoAtualizadoBD = getVeiculoBD();
        veiculoAtualizadoBD.setDisponivel(Boolean.FALSE);

        when(repository.save(any(Veiculo.class))).thenReturn(veiculoAtualizadoBD);

        VeiculoDTO veiculoDTO = new VeiculoDTO();
        veiculoDTO.setModelo(veiculoAtualizadoBD.getModelo());
        veiculoDTO.setMarca(veiculoAtualizadoBD.getMarca());
        veiculoDTO.setPlaca(veiculoAtualizadoBD.getPlaca());
        veiculoDTO.setDisponivel(veiculoAtualizadoBD.getDisponivel());
        veiculoDTO.setDataFabricacao(veiculoAtualizadoBD.getDataFabricacao());

        Response<VeiculoDTO> response = new Response<>("Sucesso", veiculoDTO);
        assertEquals(ResponseEntity.ok(response), service.atualizar(veiculoDTO));
    }

    @Test
    @DisplayName("Deve retornar erro placa invalida ao atualizar veiculo")
    public void deveRetornarErroAoAtualizarVeiculo() {
        VeiculoDTO veiculoAtualizadoDTO = getVeiculoAtualizadoDTO();
        veiculoAtualizadoDTO.setPlaca("ABC1234");
        doCallRealMethod().when(validacaoPlaca).isPlacaValida(anyString());
        Response<Boolean> response = new Response<Boolean>("Placa invalida!", null);
        assertEquals(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response), service.atualizar(veiculoAtualizadoDTO));
    }

    @Test
    @DisplayName("Deve retornar erro veiculo não encontrado ao atualizar")
    public void deveRetornarErroNaoencontradoAoAtualizarVeiculo() {
        doCallRealMethod().when(validacaoPlaca).isPlacaValida(anyString());
        Response<Boolean> response = new Response<Boolean>("Veículo não encontrado", null);
        assertEquals(ResponseEntity.status(HttpStatus.NO_CONTENT).body(response), service.atualizar(getVeiculoAtualizadoDTO()));
    }

    @Test
    @DisplayName("Deve retornar erro veiculo nulo")
    public void deveRetornarErroVeiculoNulo() {
        VeiculoDTO veiculoDTO = new VeiculoDTO();
        doCallRealMethod().when(validacaoPlaca).isPlacaValida(anyString());
        when(repository.findByPlaca(anyString())).thenThrow(NullPointerException.class);
        assertThrows(Exception.class, () -> service.atualizar(veiculoDTO));
    }

    @Test
    @DisplayName("Deve retornar lista de veiculos")
    public void deveRetornarListaDeVeiculos() {
        when(repository.findAll()).thenReturn(List.of(getVeiculoBD()));
        List<VeiculoDTO> veiculoDTOS = List.of(getVeiculoDTO());
        Response<List<VeiculoDTO>> response = Response.<List<VeiculoDTO>>builder().message("Sucesso").detail(veiculoDTOS).build();
        assertEquals(ResponseEntity.ok(response), service.listarTodos());
    }

    @Test
    @DisplayName("Deve Retornar veiculo pela Placa.")
    public void deveRetornarVeiculoPelaPlaca() {
        String placa = "XYZ-4578";
        when(repository.findByPlaca(anyString())).thenReturn(Optional.of(getVeiculoBD()));
        assertEquals(Optional.of(getVeiculoBD()), service.buscarVeiculoPelaPlaca(placa));
    }


    private static VeiculoDTO getVeiculoDTO() {
        VeiculoDTO veiculoDTO = new VeiculoDTO();
        veiculoDTO.setPlaca("XYZ-4578");
        veiculoDTO.setModelo("F40");
        veiculoDTO.setMarca("FERRARI");
        veiculoDTO.setDisponivel(Boolean.TRUE);
        veiculoDTO.setDataFabricacao(LocalDate.parse("2000-01-01"));
        return veiculoDTO;
    }

    private static VeiculoDTO getVeiculoAtualizadoDTO() {
        VeiculoDTO veiculoDTO = new VeiculoDTO();
        veiculoDTO.setPlaca("XYZ-4588");
        veiculoDTO.setModelo("F40");
        veiculoDTO.setMarca("FERRARI");
        veiculoDTO.setDisponivel(Boolean.FALSE);
        veiculoDTO.setDataFabricacao(LocalDate.parse("2020-05-10"));
        return veiculoDTO;
    }

    private static Veiculo getVeiculoBD() {
        Veiculo veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setPlaca("XYZ-4578");
        veiculo.setModelo("F40");
        veiculo.setMarca("FERRARI");
        veiculo.setDisponivel(Boolean.TRUE);
        veiculo.setDataFabricacao(LocalDate.parse("2000-01-01"));
        return veiculo;
    }

}