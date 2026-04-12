# Git Workflow — SuinoGestor

## Modelo: GitHub Flow

### Regras

- `main` é sempre estável e deployável
- Todo trabalho novo acontece em uma **feature branch** criada a partir de `main`
- Nomenclatura de branches: `feature/<nome-kebab-case>` para features, `fix/<nome-kebab-case>` para correções
- Commits em português, descritivos e no imperativo: `"Adiciona tela de cadastro de fêmeas"`
- Ao concluir, abrir Pull Request para `main`
- Merge somente após o build passar sem erros

### Convenção de branches por spec

Cada spec do `.kiro/specs/` corresponde a uma branch:

| Spec | Branch |
|---|---|
| `cadastro-femeas` | `feature/cadastro-femeas` |
| `refatoracao-ui-cadastro-femeas` | `fix/refatoracao-ui-cadastro-femeas` |
| `ciclo-reprodutivo-femea` | `feature/ciclo-reprodutivo-femea` |

### Commits

Formato: `<tipo>: <descrição em português>`

Tipos:
- `feat:` nova funcionalidade
- `fix:` correção de bug
- `refactor:` refatoração sem mudança de comportamento
- `test:` adição ou correção de testes
- `chore:` tarefas de build, dependências, configuração
