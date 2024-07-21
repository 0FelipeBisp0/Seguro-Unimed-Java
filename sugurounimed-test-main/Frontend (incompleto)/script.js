document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('customer-form');
    const resultDiv = document.getElementById('result');

    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        const name = document.getElementById('name').value;
        const email = document.getElementById('email').value;
        const gender = document.getElementById('gender').value;
        const cep = document.getElementById('cep').value;

        try {
            // Cadastrar cliente
            const response = await fetch('http://localhost:8080/customers', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    name: name,
                    email: email,
                    gender: gender,
                    cep: cep
                })
            });

            if (!response.ok) {
                throw new Error('Erro ao cadastrar cliente.');
            }

            const result = await response.json();
            const customerId = result.id;

            // Exibir o endereço do cliente cadastrado
            const customerResponse = await fetch(`http://localhost:8080/customers/${customerId}`);
            if (!customerResponse.ok) {
                throw new Error('Erro ao buscar detalhes do cliente.');
            }

            const customer = await customerResponse.json();

            let resultHtml = `<h2>Cliente Cadastrado</h2>`;
            resultHtml += `<p><strong>Nome:</strong> ${customer.name}</p>`;
            resultHtml += `<p><strong>Email:</strong> ${customer.email}</p>`;
            resultHtml += `<p><strong>Gênero:</strong> ${customer.gender}</p>`;

            if (customer.enderecos && customer.enderecos.length > 0) {
                resultHtml += `<h3>Endereços:</h3>`;
                customer.enderecos.forEach(address => {
                    resultHtml += `<p><strong>CEP:</strong> ${address.cep}</p>`;
                    resultHtml += `<p><strong>Logradouro:</strong> ${address.logradouro}</p>`;
                    resultHtml += `<p><strong>Bairro:</strong> ${address.bairro}</p>`;
                    resultHtml += `<p><strong>Cidade:</strong> ${address.cidade}</p>`;
                    resultHtml += `<p><strong>Estado:</strong> ${address.estado}</p>`;
                });
            } else {
                resultHtml += `<p>Nenhum endereço encontrado.</p>`;
            }

            resultDiv.innerHTML = resultHtml;
        } catch (error) {
            console.error('Erro:', error);
            resultDiv.innerHTML = `<p style="color: red;">Erro: ${error.message}</p>`;
        }
    });
});
