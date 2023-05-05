package br.com.ada.testeautomatizado.service;

import br.com.ada.testeautomatizado.dto.VeiculoDTO;
import br.com.ada.testeautomatizado.exception.PlacaInvalidaException;
import br.com.ada.testeautomatizado.exception.VeiculoNaoEncontradoException;
import br.com.ada.testeautomatizado.model.Veiculo;
import br.com.ada.testeautomatizado.repository.VeiculoRepository;
import br.com.ada.testeautomatizado.util.Response;
import br.com.ada.testeautomatizado.util.ValidacaoPlaca;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private ValidacaoPlaca validacaoPlaca;


    public ResponseEntity<Response<VeiculoDTO>> cadastrar(VeiculoDTO veiculoDTO) {
        try {
            this.validacaoPlaca.isPlacaValida(veiculoDTO.getPlaca());
            Veiculo veiculo = new Veiculo();
            veiculo.setPlaca(veiculoDTO.getPlaca());
            veiculo.setModelo(veiculoDTO.getModelo());
            veiculo.setMarca(veiculoDTO.getMarca());
            veiculo.setDataFabricacao(veiculoDTO.getDataFabricacao());
            veiculo.setDisponivel(veiculoDTO.getDisponivel());
            this.veiculoRepository.save(veiculo);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<VeiculoDTO>("Sucesso", veiculoDTO));
        } catch (PlacaInvalidaException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new Response<VeiculoDTO>(e.getMessage(), null));
        } catch (Exception e) {
            throw e;
        }
    }

    public ResponseEntity<Response<Boolean>> deletarVeiculoPelaPlaca(String placa) {
        try {
            this.validacaoPlaca.isPlacaValida(placa);
            Optional<Veiculo> getVeiculo = veiculoRepository.findByPlaca(placa);
            getVeiculo.orElseThrow(VeiculoNaoEncontradoException::new);
            veiculoRepository.delete(getVeiculo.get());
            Response<Boolean> response = new Response<>("Sucesso", Boolean.TRUE);
            return ResponseEntity.ok(response);
        } catch (PlacaInvalidaException | VeiculoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new Response<Boolean>(e.getMessage(), Boolean.FALSE));
        } catch (Exception e) {
            throw e;
        }
    }

    public ResponseEntity<Response<VeiculoDTO>> atualizar(VeiculoDTO veiculoDTO) {
        try {
            this.validacaoPlaca.isPlacaValida(veiculoDTO.getPlaca());
            Optional<Veiculo> getVeiculo = veiculoRepository.findByPlaca(veiculoDTO.getPlaca());
            getVeiculo.orElseThrow(VeiculoNaoEncontradoException::new);
            Veiculo veiculo = getVeiculo.get();
            veiculo.setPlaca(veiculoDTO.getPlaca());
            veiculo.setModelo(veiculoDTO.getModelo());
            veiculo.setMarca(veiculoDTO.getMarca());
            veiculo.setDataFabricacao(veiculoDTO.getDataFabricacao());
            veiculo.setDisponivel(veiculoDTO.getDisponivel());

            Veiculo veiculoSave = this.veiculoRepository.save(veiculo);

            VeiculoDTO veiculoDTOSave = new VeiculoDTO();
            veiculoDTOSave.setModelo(veiculoSave.getModelo());
            veiculoDTOSave.setMarca(veiculoSave.getMarca());
            veiculoDTOSave.setPlaca(veiculoSave.getPlaca());
            veiculoDTOSave.setDisponivel(veiculoSave.getDisponivel());
            veiculoDTOSave.setDataFabricacao(veiculoSave.getDataFabricacao());

            return ResponseEntity.ok(new Response<VeiculoDTO>("Sucesso", veiculoDTOSave));
        } catch (PlacaInvalidaException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new Response<VeiculoDTO>(e.getMessage(), null));
        } catch (VeiculoNaoEncontradoException e){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new Response<>(e.getMessage(), null));
        }catch (Exception e) {
            throw e;
        }
    }

    public ResponseEntity<Response<List<VeiculoDTO>>> listarTodos() {
        List<Veiculo> listaVeiculos = veiculoRepository.findAll();
        List<VeiculoDTO> listaVeiculosDTO = listaVeiculos.stream().map(veiculo -> {
            VeiculoDTO veiculoDTO = new VeiculoDTO();
            veiculoDTO.setModelo(veiculo.getModelo());
            veiculoDTO.setMarca(veiculo.getMarca());
            veiculoDTO.setPlaca(veiculo.getPlaca());
            veiculoDTO.setDisponivel(veiculo.getDisponivel());
            veiculoDTO.setDataFabricacao(veiculo.getDataFabricacao());
            return veiculoDTO;
        }).toList();
        Response<List<VeiculoDTO>> response = Response.<List<VeiculoDTO>>builder().message("Sucesso").detail(listaVeiculosDTO).build();
        return ResponseEntity.ok(response);
    }

    public Optional<Veiculo> buscarVeiculoPelaPlaca(String placa) {
        return this.veiculoRepository.findByPlaca(placa);
    }
}

