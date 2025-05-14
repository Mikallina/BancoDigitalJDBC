function pesquisarCliente() {
	const cpf = document.getElementById('cpf').value;

	fetch(`http://localhost:8080/cliente/buscarCpf/${cpf}`)
		.then(response => response.json())
		.then(data => {
			console.log(data)
			if (data) {
				document.getElementById('clienteNome').textContent = data.nome;
				document.getElementById('clienteCpf').textContent = data.cpf;
				document.getElementById('clienteInfo').style.display = 'block';
			} else {
				alert("Cliente não encontrado.");
			}
		})
		.catch(error => alert("Erro ao buscar cliente."));
}
