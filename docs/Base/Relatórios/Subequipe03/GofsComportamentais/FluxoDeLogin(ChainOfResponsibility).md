# GoF Comportamental: Chain of Responsibility — Subsistema de Login

## 1. Contextualização e Justificativa Arquitetural

No escopo do **G1_ProjetoComercioEletronico**, o subsistema de autenticação (Login) exige a execução de múltiplas verificações de segurança sequenciais e interdependentes antes da concessão de um token de sessão ao usuário.

A partir dos insumos obtidos nas entregas anteriores de **Engenharia Reversa** e **Modelagem Dinâmica (Diagramas de Sequência e de Comunicação)**, foram constatados comportamentos críticos de negócio documentados nos achados:

- **Achado-L01 (Validação de Risco/Captcha no Page Load):** O script do reCAPTCHA Enterprise é carregado preventivamente já no _page load_, gerando um token que deve ser validado antes do processamento das credenciais.
- **Achado-L02 (Curto-circuito por Credenciais Inválidas):** Caso o identificador ou a senha não correspondam aos registros do Banco de Dados, o fluxo é imediatamente interrompido, retornando uma resposta genérica comprimida (mantendo status HTTP 200) e exigindo a renovação do desafio Captcha.
- **Achado-L03 (2FA Condicional Baseado em Score de Risco):** A etapa de autenticação de dois fatores (envio de código via SMS/E-mail) não é executada de maneira estática para todos os acessos. Quando o score de risco avaliado é baixo, o fluxo prossegue silenciosamente sem interromper a experiência do usuário legítimo.

Para evitar o acoplamento rígido de estruturas condicionais aninhadas (`if/else`) dentro do `ServicoDeAutenticacao`, aplicou-se o padrão de projeto comportamental **Chain of Responsibility** (GAMMA et al., 2000). Esse padrão permite desacoplar o remetente de uma requisição (`ServicoDeAutenticacao`) de seus destinatários, encadeando objetos validadores (`ValidadorHandler`) onde cada elo decide autonomamente se aprova e repassa a requisição adiante ou se interrompe o fluxo em caso de violação de segurança.

---

## 2. Modelagem UML (Padrão Chain of Responsibility)

Abaixo apresenta-se o diagrama de classes UML modelado em PlantUML, evidenciando os participantes do padrão aplicado ao Subsistema de Login:

![Modelo GoF Comportamental: Chain of Responsibility - Subsistema de Login](../assets/chain_responsa_Login.png)

### Participantes do Padrão na Modelagem:

1. **`ValidadorHandler` (Handler Abstrato):** Define a interface comum para o processamento das requisições (`processarRequisicao`) e mantém a referência de agregação (`proximo`) para o elo sucessor da cadeia, implementando o encadeamento fluido por meio de `setProximo()`.
2. **`ValidadorCaptcha`, `ValidadorCredenciais`, `ValidadorRisco`, `ValidadorTwoFactor` (Concrete Handlers):** Isolam cada regra de validação em uma classe dedicada (respeitando o _Single Responsibility Principle_ - SRP e o _Open/Closed Principle_ - OCP).
3. **`RequisicaoLogin` (Contexto / Request):** Encapsula os dados da tentativa de autenticação (`email`, `senhaHash`, `tokenCaptcha`, `codigo2FA` e `metadadosDispositivo`), garantindo uma assinatura única por toda a cadeia.
4. **`ServicoDeAutenticacao` (Client):** Configura a topologia da cadeia de validação e dispara a verificação invocando exclusivamente o primeiro elo.

---

## 3. Implementação em Java

Os códigos-fonte encontram-se estruturados nos pacotes `context`, `handler` e `service` dentro do diretório [`src/comportamentais/FluxoDeLogin(ChainOfResponsibility)/`](<../src/comportamentais/FluxoDeLogin(ChainOfResponsibility)/>).

### 3.1. Pacote `context` — [`RequisicaoLogin.java`](<../src/comportamentais/FluxoDeLogin(ChainOfResponsibility)/context/RequisicaoLogin.java>)

```java
package context;

public class RequisicaoLogin {
    private String email;
    private String senhaHash;
    private String tokenCaptcha;
    private String codigo2FA;
    private Object metadadosDispositivo;
    private boolean exige2FA;

    public RequisicaoLogin(String email, String senhaHash, String tokenCaptcha,
                           String codigo2FA, Object metadadosDispositivo) {
        this.email = email;
        this.senhaHash = senhaHash;
        this.tokenCaptcha = tokenCaptcha;
        this.codigo2FA = codigo2FA;
        this.metadadosDispositivo = metadadosDispositivo;
        this.exige2FA = false;
    }

    public String getEmail() { return email; }
    public String getSenhaHash() { return senhaHash; }
    public String getTokenCaptcha() { return tokenCaptcha; }
    public String getCodigo2FA() { return codigo2FA; }
    public Object getMetadadosDispositivo() { return metadadosDispositivo; }
    public boolean isExige2FA() { return exige2FA; }
    public void setExige2FA(boolean exige2FA) { this.exige2FA = exige2FA; }
}
```

### 3.2. Pacote `handler` — Handler Abstrato e Validadores Concretos

- **[`ValidadorHandler.java`](<../src/comportamentais/FluxoDeLogin(ChainOfResponsibility)/handler/ValidadorHandler.java>)**:

```java
package handler;

import context.RequisicaoLogin;

public abstract class ValidadorHandler {
    private ValidadorHandler proximo;

    public ValidadorHandler setProximo(ValidadorHandler handler) {
        this.proximo = handler;
        return handler;
    }

    public abstract boolean processarRequisicao(RequisicaoLogin req);

    protected boolean passarParaOProximo(RequisicaoLogin req) {
        if (this.proximo == null) {
            return true;
        }
        return this.proximo.processarRequisicao(req);
    }
}
```

- **[`ValidadorCaptcha.java`](<../src/comportamentais/FluxoDeLogin(ChainOfResponsibility)/handler/ValidadorCaptcha.java>)**, **[`ValidadorCredenciais.java`](<../src/comportamentais/FluxoDeLogin(ChainOfResponsibility)/handler/ValidadorCredenciais.java>)**, **[`ValidadorRisco.java`](<../src/comportamentais/FluxoDeLogin(ChainOfResponsibility)/handler/ValidadorRisco.java>)** e **[`ValidadorTwoFactor.java`](<../src/comportamentais/FluxoDeLogin(ChainOfResponsibility)/handler/ValidadorTwoFactor.java>)**: Implementam cada etapa de verificação refletindo os Achados L01, L02 e L03.

### 3.3. Pacote `service` — [`ServicoDeAutenticacao.java`](<../src/comportamentais/FluxoDeLogin(ChainOfResponsibility)/service/ServicoDeAutenticacao.java>)

```java
package service;

import context.RequisicaoLogin;
import handler.*;

public class ServicoDeAutenticacao {
    private ValidadorHandler cadeiaValidacao;

    public ServicoDeAutenticacao() {
        ValidadorHandler captcha = new ValidadorCaptcha();
        ValidadorHandler credenciais = new ValidadorCredenciais();
        ValidadorHandler risco = new ValidadorRisco();
        ValidadorHandler twoFactor = new ValidadorTwoFactor();

        captcha.setProximo(credenciais)
               .setProximo(risco)
               .setProximo(twoFactor);

        this.cadeiaValidacao = captcha;
    }

    public boolean autorizar(RequisicaoLogin req) {
        return cadeiaValidacao.processarRequisicao(req);
    }
}
```

---

## 4. Rastreabilidade com Artefatos Anteriores

| Artefato de Origem                               | Elemento Rastro                                                 | Reflexo no Padrão Chain of Responsibility                                  |
| :----------------------------------------------- | :-------------------------------------------------------------- | :------------------------------------------------------------------------- |
| **Engenharia Reversa / Diagrama de Sequência**   | **Achado-L01** (Script de risco no page load)                   | Isolado no primeiro elo concreto (`ValidadorCaptcha`).                     |
| **Engenharia Reversa / Diagrama de Comunicação** | **Achado-L02** (Erro genérico HTTP 200 em falha de credenciais) | Tratado com interrupção imediata da cadeia em `ValidadorCredenciais`.      |
| **NFR Framework (Usabilidade vs. Segurança)**    | **Achado-L03** (Fluxo silencioso para baixo risco)              | Implementado pela transição entre `ValidadorRisco` e `ValidadorTwoFactor`. |

---

## 5. Referências Bibliográficas

- GAMMA, Erich; HELM, Richard; JOHNSON, Ralph; VLISSIDES, John. **Padrões de Projetos: Soluções Reutilizáveis de Software Orientados a Objetos**. Porto Alegre: Bookman, 2000.
- REFACTORING GURU. **Chain of Responsibility**. Disponível em: https://refactoring.guru/pt-br/design-patterns/chain-of-responsibility.

---

## 6. Histórico de Versão

| Versão |    Data    | Descrição                                                                                            | Autor(es)            | Revisor(es)  |
| :----: | :--------: | :--------------------------------------------------------------------------------------------------- | :------------------- | :----------- |
| `1.0`  | 15/10/2026 | Elaboração da modelagem PlantUML e implementação Java do padrão Chain of Responsibility para o Login | Pedro Henrique Gomes | SubEquipe 03 |
