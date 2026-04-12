# Documento de Requisitos — Exportação e Relatórios da Ficha Individual

## Introdução

Este módulo implementa a geração e exportação de relatórios em PDF no SuinoGestor. Permite ao produtor compartilhar dados com veterinários, integradores ou bancos, exportando a ficha individual de fêmeas e reprodutores, além de um resumo consolidado do plantel.

---

## Glossário

- **Matriz**: Fêmea suína adulta que já pariu ao menos uma vez.
- **Varrão / Reprodutor**: Macho suíno utilizado em monta natural ou inseminação artificial.
- **Plantel**: Conjunto total de animais reprodutivos da granja.
- **Sistema**: O aplicativo SuinoGestor.
- **Produtor**: Usuário do sistema; responsável pela gestão da granja.
- **Ficha_da_Matriz**: Registro digital individual de uma fêmea, contendo todo o histórico reprodutivo.
- **Ficha_do_Reprodutor**: Registro digital individual de um varrão, contendo histórico de uso e avaliações.
- **Painel_do_Plantel**: Tela principal do módulo, exibindo visão consolidada do rebanho reprodutivo.

---

## Requisitos

### Requisito 12: Exportação e Relatórios da Ficha Individual

**User Story:** Como produtor, quero exportar a ficha individual de uma fêmea ou reprodutor e gerar relatórios do plantel, para que eu possa compartilhar dados com o veterinário, o integrador ou o banco.

#### Critérios de Aceitação

1. THE Sistema SHALL gerar a Ficha_da_Matriz em formato PDF contendo: dados de identificação, histórico completo de eventos reprodutivos (coberturas, gestações, partos, desmames), histórico de ECC, alertas gerados e indicadores individuais calculados.
2. THE Sistema SHALL gerar a Ficha_do_Reprodutor em formato PDF contendo: dados de identificação, histórico de uso (coberturas por semana/mês), ECC e alertas gerados.
3. THE Sistema SHALL gerar o relatório "Resumo do Plantel" em formato PDF contendo: todos os indicadores do Painel_do_Plantel, distribuição por paridade, taxa de reposição e lista de fêmeas por status.
4. WHEN o Produtor solicitar a exportação de um relatório, THE Sistema SHALL gerar o arquivo em até 10 segundos para plantéis de até 500 fêmeas.
5. THE Sistema SHALL permitir o compartilhamento do PDF gerado via aplicativos de mensagem instalados no dispositivo móvel do Produtor.
