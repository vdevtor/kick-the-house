# Kick The House

Kick The House é um aplicativo que calcula as probabilidades de jogos de futebol entre dois times com base em seus últimos cinco jogos contra qualquer adversário. Ele também possui um modo manual onde o usuário pode inserir as odds, valores e probabilidades calculadas por si mesmo.

## Características Principais

- Calcula as probabilidades de jogos de futebol com base nas últimas cinco partidas dos times.
- Modo manual para inserção personalizada de odds, valores e probabilidades.
- Calcula o valor esperado positivo (EV+) em apostas.

## O que é Valor Esperado Positivo (EV+)?

O Valor Esperado Positivo (EV+) é uma medida usada em apostas para determinar se uma aposta tem um valor positivo a longo prazo. Em termos simples, se o EV+ de uma aposta for maior que zero, isso indica que a aposta tem um retorno esperado positivo a longo prazo. Por exemplo, se o EV+ de uma aposta for 0,5, isso significa que, em média, você espera ganhar 50 centavos a cada real apostado.

## Tecnologias Utilizadas

- Room Database: Biblioteca para armazenamento e gerenciamento de dados locais no Android.
- Dagger Hilt: Biblioteca para injeção de dependência no Android, que facilita a organização e reutilização de código.
- Clean Code: Princípios e práticas para escrever um código limpo, legível e de fácil manutenção.
- MVVM (Model-View-ViewModel): Padrão de arquitetura que separa a lógica de negócio da interface do usuário.
- Custom Components: Componentes personalizados para uma experiência de usuário única e intuitiva.
- Modularização: Abordagem de dividir o aplicativo em módulos independentes para facilitar o desenvolvimento, teste e manutenção.

## Estrutura do Projeto

O projeto Kick The House é dividido em três módulos principais:

1. **app**: Módulo do aplicativo principal que contém a lógica da interface do usuário e a interação com o usuário.
2. **core**: Módulo que possui 3 bibliotecas A. **Componenets** Módulo de código central que inclui resources,custom views compartilhados em todo o projeto, B - **Domain** Responsavél por modelo das classes e os contratos (interfaces) para implementação dos repositorios, C - **Use_Cases** Módulo responsavél por executar ações propostas pelo usuario, assim validando as mesmas.
3. **data**: Módulo de dados que abrange a camada de acesso a dados, como a manipulação de banco de dados.

## Como Contribuir

Se você quiser contribuir para o Kick The House, fique à vontade para seguir as etapas abaixo:

1. Faça um fork deste repositório.
2. Crie uma nova branch com uma descrição clara do que você está trabalhando.
3. Desenvolva e teste suas alterações.
4. Envie um pull request descrevendo suas alterações em detalhes.

## Licença

O Kick The House é licenciado sob a [MIT License](LICENSE.md).

---

Esperamos que o Kick The House seja útil para calcular probabilidades futebolísticas e ajudar nas apostas esportivas. Se você tiver alguma dúvida, sugestão ou problema, sinta-se à vontade para abrir uma [issue](https://github.com/vdevtor/kick-the-house/issues) neste repositório. Obrigado por usar o Kick The House!
