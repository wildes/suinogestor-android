# Project Structure

## Current State

The repository contains only domain documentation. No application code exists yet.

```
/
├── README.md              # Executive summary of swine production strategies
└── docs/                  # Domain knowledge base (Brazilian Portuguese)
    ├── gestao-360.md              # Full product vision and module descriptions
    ├── sistema-lotes.md           # Batch management system explained
    ├── producao-planejamento.md   # Production planning
    ├── manejo-reprodutivo.md      # Reproductive management
    ├── manejo-leitoes.md          # Piglet handling
    ├── ficha-da-matriz.md         # Individual sow record
    ├── ficha-do-lote.md           # Batch record
    ├── distribuicao-ideal-matrizes.md  # Ideal sow distribution by parity
    ├── alvos-de-producao.md       # Production targets
    ├── dimensionamento.md         # Farm sizing calculations
    ├── consumo-racao.md           # Feed consumption
    ├── nutricao-agua.md           # Water and nutrition
    ├── custo-producao.md          # Production costs
    ├── aspectos-economicos.md     # Economic aspects
    ├── desempenho-cevado.md       # Finishing performance
    ├── performance-2-parto.md     # Second parity performance
    ├── prenhez.md                 # Pregnancy management
    ├── repeticao-cio.md           # Return to estrus
    ├── sobrevivencia.md           # Survival rates
    ├── score-corporal.md          # Body condition scoring
    ├── sanidade-saude.md          # Health and sanitation
    ├── tabelas-de-cobertura.md    # Coverage tables
    └── tabelas-de-desempenho.md   # Performance tables
```

## Conventions

- All `docs/` files are reference material — treat them as the source of truth for domain logic and business rules
- File names use kebab-case in Portuguese
- When implementing features, cross-reference the relevant `docs/` files for correct formulas, thresholds, and terminology
