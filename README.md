# 📊 FinanceApp

**FinanceApp** é um aplicativo Android nativo, desenvolvido em Kotlin, para o gerenciamento de finanças pessoais. Ele permite ao usuário registrar, visualizar, editar e deletar suas receitas e despesas, organizá-las por categoria e visualizar um resumo financeiro através de um dashboard com gráficos.

<p align="center">
  <img src="https://github.com/user-attachments/assets/c01aa425-171d-4e11-940d-0e9948e597f3" width="200">
  <img src="https://github.com/user-attachments/assets/e7900b6b-038b-441a-85ce-b9bb6ea4304e" width="200">
  <img src="https://github.com/user-attachments/assets/47c8cf2d-100d-4077-93d3-180579af7375" width="200">
  <img src="https://github.com/user-attachments/assets/844dbca6-fc79-4c41-b25e-930a70f15cec" width="200">
</p>

---

## 🚀 Funcionalidades

- **CRUD Completo:** Cadastro, listagem, atualização e remoção de transações e categorias.
- **Dashboard Interativo:** Exibe um resumo com Saldo Atual, Total de Receitas e Total de Despesas.
- **Visualização com Gráficos:** Gráfico de pizza que mostra a distribuição de despesas por categoria.
- **Filtragem Avançada:**
    - Filtro de transações por período (Mês Atual, Mês Passado, Todos).
    - Filtro de transações por tipo (Receitas ou Despesas) a partir do dashboard.
- **Busca em Tempo Real:** Barra de busca para filtrar transações pela descrição.
- **Persistência Local:** Todos os dados são salvos em um banco de dados local no dispositivo.
- **UI Moderna:** Interface construída com Material Design 3, incluindo temas claro e escuro.
---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Kotlin
- **Arquitetura:**
    - MVVM (Model-View-ViewModel)
    - Single-Activity Architecture (Arquitetura de Atividade Única)
- **Componentes Jetpack:**
    - **Room Persistence Library:** Para o banco de dados SQL local.
    - **Navigation Component:** Para gerenciar a navegação entre as telas (fragments).
    - **ViewModel:** Para gerenciar o estado da UI de forma consciente do ciclo de vida.
    - **Hilt:** Para injeção de dependência em todo o aplicativo.
    - **View Binding:** Para acesso seguro aos componentes da UI.
- **Assincronismo:** Kotlin Coroutines e Flow para uma programação reativa e assíncrona.
- **UI:** Android Views com XML, Material Design 3, RecyclerView.
- **Gráficos:** MPAndroidChart
- **Sistema de Build:** Gradle

---

## 📦 Como Executar o Projeto

### Pré-requisitos

- Android Studio (versão Iguana ou mais recente recomendada)
- Um Emulador Android (API 24+) ou um dispositivo físico.

### Passos

1.  Clone o repositório:
    ```bash
    git clone [https://github.com/GuilhermeD9/FinanceApp.git](https://github.com/GuilhermeD9/FinanceApp.git)
    cd FinanceApp
    ```

2.  Abra o projeto no Android Studio.

3.  Aguarde o Gradle sincronizar todas as dependências do projeto.

4.  Execute a configuração 'app' em um emulador ou dispositivo físico conectado.

---

## 📄 Licença
Este projeto está licenciado sob a Licença MIT. Consulte o arquivo `LICENSE` para mais informações.
