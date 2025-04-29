package br.com.meubancodigitaljdbc.service;

import br.com.meubancodigitaljdbc.dao.ClienteDAO;
import br.com.meubancodigitaljdbc.model.Cliente;
import br.com.meubancodigitaljdbc.model.Endereco;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ClienteService {

    @Autowired
    private ClienteDAO clienteDAO;

    public void salvarCliente(Cliente cliente, boolean isAtualizar) throws Exception {
        validarCliente(cliente, isAtualizar);
        clienteDAO.save(cliente);
    }

    public Cliente buscarClientePorCpf(String cpf) {
        return clienteDAO.findByCpf(cpf);
    }

    public List<Cliente> listarClientes() {
        return clienteDAO.findAll();
    }

    public Optional<Cliente> findById(Long clienteId) {
        return clienteDAO.findById(clienteId);
    }

    public void deletarCliente(Long clienteId) {
        Optional<Cliente> clienteExistente = clienteDAO.findById(clienteId);
        if (clienteExistente.isPresent()) {
            clienteDAO.deleteById(clienteId); // Deleta o cliente com o ID fornecido
        }

    }

    private void validarCliente(Cliente cliente, boolean isAtualizar) throws Exception {
        if (!validarCpf(cliente.getCpf(), isAtualizar, cliente.getIdCliente())) {
            throw new Exception("CPF inválido ou já cadastrado.");
        }
        if (!validarNome(cliente.getNome())) {
            throw new Exception("Nome inválido.");
        }
        if (!validarEndereco(cliente.getEndereco())) {
            throw new Exception("Endereço inválido.");
        }
        if (!validarDataNascimento(cliente.getDataNascimento())) {
            throw new Exception("Data de nascimento inválida.");
        }

    }

    public boolean validarCpf(String cpf, boolean isAtualizar, Long clienteId) {
        ValidaCpf validaCpf = new ValidaCpf();
        if (!validaCpf.isCPF(cpf)) {
            return false;
        }

        if (isAtualizar) {
            Cliente clienteExistente = clienteDAO.findByCpf(cpf);

            if (clienteExistente != null && !clienteExistente.getIdCliente().equals(clienteId)) {
                return false;
            }
        } else {
            Cliente clienteExistente = clienteDAO.findByCpf(cpf);
            if (clienteExistente != null) {
                return false;
            }
        }
        return true;
    }

    public boolean validarNome(String nome) {
        if (nome.length() < 2 || nome.length() > 100) {
            System.out.println("O nome deve ter entre 2 e 100 caracteres.");
            return false;
        }
        if (!nome.matches("[a-zA-Z ]+")) {
            System.out.println("O nome deve conter apenas letras e espaços");
            return false;
        }
        return true;
    }

    public boolean validarEndereco(Endereco endereco) {
        if (endereco.getLogradouro().isEmpty() || endereco.getNumero() == null || endereco.getBairro().isEmpty()
                || endereco.getLocalidade().isEmpty()) {
            System.out.println("Endereço Inválido");
            return false;
        }
        return true;
    }

    public boolean validarCEP(String cep) {
        String regex = "^[0-9]{5}-[0-9]{3}$";
        Pattern pattern = Pattern.compile(regex);
        return Pattern.matches(regex,cep);
    }

    public boolean validarDataNascimento(LocalDate dataNascimento) {
        try {
            if (dataNascimento.isAfter(LocalDate.now())) {
                System.out.println("Data de nascimento inválida, não pode ser no futuro");
                return false;
            }
            int idade = calcularIdade(dataNascimento);
            if (idade < 18) {
                System.out.println("Cliente deve ter ao menos 18 anos");
                return false;
            }
            return true;

        } catch (Exception e) {
            System.out.println("Data de Nascimento inválida. O formato deve ser dd/MM/yyyy");
            return false;
        }
    }

    private int calcularIdade(LocalDate dataNascimento) {
        int idade = LocalDate.now().getYear() - dataNascimento.getYear();
        if (dataNascimento.getMonthValue() > LocalDate.now().getMonthValue()
                || (dataNascimento.getMonthValue() == LocalDate.now().getMonthValue()
                && dataNascimento.getDayOfMonth() > LocalDate.now().getDayOfMonth())) {
            idade--;
        }
        return idade;
    }

}