# Lege Artis Consulium — Bio-Legal NGS & Q-MAX Offline Cypher Suite

**Lege Artis Consulium** is an offline-first Android bioinformatics, cryptographic security, and medico-legal consulting platform. It unifies Next-Generation Sequencing (NGS) genomic analysis (interactive 3D molecules, base-call chromatograms, variant filtering, and QC metrics) with a **Local Secure Q-MAX Cypher & Random Hashing Sync Binary Scheme** backed by an on-device **Room SQLite Database**, a **Tension Reinforcement Security Metasimulator (`Task A-B-C` MAP-Processing)**, and multi-model Gemini AI capabilities.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> Review the updated architectural decisions reflecting your latest requirements for the **Q-MAX Offline Cypher DB**, **Random Hashing Sync Binary Scheme**, and **Tension Reinforcement Metasimulator**.

- **Confirmed Decision 1 — Q-MAX Offline Cypher DB Integrated into Lege Artis NGS Suite**: Replaces cloud-only storage gates with a self-contained, resilient **Room SQLite Database (`LegeArtisDatabase`)** secured by the **Q-MAX Binary Block Cypher** and **Salted SHA-256/512 Random Hashing Sync Scheme**, allowing immediate offline access to all NGS genomic data, versioned legal dossiers, and cryptographic sync packets.
- **Confirmed Decision 2 — Interactive Binary Bit-Matrix, Hash Chain & Deterministic Sync Packets**: Adds a dedicated **Q-MAX Cypher & Binary Sync Console** featuring an interactive 64/128-bit binary matrix canvas, real-time avalanche bit-flip entropy tracking, salted SHA-256 hash chain verification, and exportable/verifiable deterministic binary sync packets.
- **Confirmed Decision 3 — Tension Reinforcement & MAP-Processing Metasimulator (`Task A -> B -> C`)**: Adds a dedicated **Framework Calibration & Metasimulator Engine** implementing split-step reasoning (`Divide Reasoning`), tension-reinforcement security resilience scoring, `Task A (Lookup) -> Task B (Research/Cypher) -> Task C (Define/MAP)` pipeline execution, and single-submission (`T=1 podání`) medico-legal validation.

---

### 1. Overview & Core Concept

- **What It Does**:
  1. **NGS Genomic Analysis Workspace**: Interactive 3D molecular helix/protein canvas, A/T/C/G base-call chromatogram viewer, high-density variant table (filtering by $p$-value, allele frequency VAF, Phred Q-score, and ACMG class), sample QC metrics, and *Lege Artis* medico-legal risk & single-submission (`T=1 podání`) calculator.
  2. **Q-MAX Offline Cypher & Random Hashing Sync Engine**: Encrypts local genomic & legal records using salted SHA-256/512 hash chains and AES-GCM / Q-MAX binary block transformations, visualizing the raw binary bit-matrix and generating deterministic binary sync packets for offline verification.
  3. **Tension Reinforcement Metasimulator (`Task A-B-C` MAP-Processing)**: Simulates adversarial security tension vs. cryptographic reinforcement across platform-agnostic pipeline stages (`Lookup -> Research -> Define MAP`) and generates structured bio-legal audit & semantic compliance findings.
  4. **Versioned Consulting Portal, RBAC & Real-Time Local Audit Log**: Tracks immutable semantic versions (`v1.0`, `v1.1`) with cryptographic hashes, encrypted client portal preview tokens, role-based access control (*Lead Bioinformatician*, *Clinical Geneticist*, *Legal Counsel*, *Client Reviewer*), and a live Room-backed activity stream.
  5. **Multi-Model Gemini AI Studio**: Integrates High-Thinking reasoning (`gemini-4-argon` with `ThinkingLevel.HIGH`), Search-Grounded variant/regulatory lookup (`gemini-3.8-flash` + Google Search), Low-Latency lab copilot (`gemini-3.1-flash-lite`), and 1K/2K/4K molecular/report visual generation (`gemini-3-pro-image-preview`).

---

### 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Zero-Friction Offline Launch**: Launches directly into the pre-seeded, Room-backed **Lege Artis NGS & Q-MAX Workspace** with an instant Dark/Light laboratory mode toggle and active role selector in the top bar.
  2. **NGS Analysis & 3 Synchronized Micro-Interactions**:
     - *Chromatogram Peak Scrubbing & Phred Magnification Loupe*: Dragging across the A/T/C/G curves magnifies local base calls, displays Phred quality ($Q \ge 30$ vs. $Q < 20$ sequencing artifact zones), and highlights the corresponding codon on the 3D helix.
     - *Cross-Widget Locus Pulse*: Selecting a gene variant row rotates the 3D molecule with spring physics to center the mutated locus and scrolls the chromatogram to that exact coordinate.
     - *Live Threshold Curtain*: Dragging $p$-value or VAF sliders sweeps a translucent laser curtain across the Volcano distribution plot with live retained/filtered variant counters.
  3. **Q-MAX Binary Cypher & Hash Sync Console**: Users input or select any genomic variant or legal document payload, inspect the live 8x16 binary bit-matrix, trigger salted random hash sync rounds, toggle individual bits to observe the SHA-256 avalanche effect, and commit deterministic binary sync packets to Room.
  4. **Tension Reinforcement Metasimulator & AI Co-Pilot**: Users run the `Task A (Lookup) -> Task B (Research) -> Task C (Define MAP)` calibration pipeline, adjust adversarial tension vs. reinforcement learning parameters, and invoke the multi-model Gemini AI suite or offline *Lege Artis* semantic analyzer.

- **Visual Identity & Theme**:
  - *Aesthetic Direction*: **Precision Bio-Laboratory & Cryptographic Instrument**—high-contrast scientific telemetry paired with clean monospaced binary matrices and authoritative editorial typography.
  - *Color Palette & Microscope-Safe Dual Mode*:
    - **Dark Mode (Default — Darkroom & Microscope Ergonomics)**:
      - `--background`: `#090D14` (Deep Optical Obsidian)
      - `--surface`: `#111823` (Instrument Slate) / `--surface-elevated`: `#1A2433`
      - `--primary`: `#38BDF8` (Cerenkov Cyan)
      - `--secondary`: `#818CF8` (Helix Indigo)
      - `--accent`: `#10B981` (Q-MAX Cypher Emerald)
    - **Light Mode (Daylight Clinical & Legal Review)**:
      - `--background`: `#F4F7FA` (Sterile Alabaster)
      - `--surface`: `#FFFFFF` (Clinical Paper) / `--surface-elevated`: `#EBF0F5`
      - `--primary`: `#0284C7` (Deep Cobalt)
      - `--secondary`: `#4F46E5` (Legal Ultramarine)
    - **Critical Semantic & Nucleotide Tokens**:
      - **Pathogenic Variant (Critical Clinical Alert)**: `#EF4444` (Crimson Pulse)
      - **Sequencing Error / Low-QC Artifact (Technical Warning)**: `#F59E0B` (Amber Warning)
      - **Likely Benign / Reference Allele**: `#10B981` (Bio-Emerald)
      - **Variant of Uncertain Significance (VUS)**: `#A855F7` (Amethyst)
  - *Typography & Hierarchy*:
    - **Display / Headers**: `Space Grotesk` (bundled locally via `font-util`)
    - **Body / Legal Copy**: `Plus Jakarta Sans`
    - **Genomic Coordinates / Q-MAX Binary / Hashes**: `JetBrains Mono`

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Offline-First Room SQLite Persistence with Deterministic Q-MAX Cypher Layer**
  - *Chosen Approach*: Uses Jetpack **Room (`@Database`, `@Dao`, `Flow<List<T>>`)** with KSP as the primary local source of truth for all variants, versioned documents, binary sync packets, metasimulator runs, and audit logs.
  - *Why*: Fulfils the explicit `Database - Offline APP` and `Local Secure Q-MAX Cypher` mandate, ensuring 100% deterministic offline operation without blocking on external sign-in gates.

- **Decision 2: Real On-Device Cryptographic Primitives + Q-MAX Binary Matrix Engine**
  - *Chosen Approach*: Combines standard JVM `MessageDigest` (`SHA-256`, `SHA-512`), `SecureRandom` salt generation, and `Cipher` (`AES/GCM/NoPadding`) with a transparent Q-MAX binary block permutation and bit-matrix visualizer.
  - *Why*: Provides authentic cryptographic integrity for document versioning and binary sync packets while making the bit-level hashing and sync mechanics visually inspectable.

- **Decision 3: Split-Step Reasoning (`Divide Reasoning`) & Metasimulator Pipeline**
  - *Chosen Approach*: Structures complex analysis into explicit, inspectable stages (`Task A: Lookup & Telemetry -> Task B: Cypher & Tension Calibration -> Task C: MAP Definition & Legal Synthesis`), supported both by deterministic local algorithms and optional Firebase AI Logic (`gemini-4-argon`, `gemini-3.8-flash`, `gemini-3.1-flash-lite`, `gemini-3-pro-image-preview`).
  - *Why*: Guarantees resilience and full functionality whether the device is operating completely air-gapped/offline or connected to Gemini AI endpoints.

---

### 4. Technical Architecture & Data Strategy *(Technical Reference)*

- **Architecture & Component Diagram**:

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                     MainActivity (Edge-to-Edge M3 UI)                        │
│  • configureAppCheck(this, intent) [For optional Gemini AI Logic endpoints]  │
│  • Microscope-Safe Dark (#090D14) / Clinical Light (#F4F7FA) Theme Switcher  │
└──────────────────────────────────────┬───────────────────────────────────────┘
                                       │
                                       ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                 LegeArtisViewModel & QMaxCypherViewModel                     │
│  • Reactive StateFlows from Room DAOs + Derived Filter & Cypher States       │
│  • Q-MAX Binary Engine (Salted SHA-256/512, AES-GCM, Bit-Matrix, Sync Frame) │
│  • Tension Reinforcement Metasimulator (Task A -> Task B -> Task C MAP)      │
└───────────────┬──────────────────────────────────────────────┬───────────────┘
                │                                              │
                ▼                                              ▼
┌───────────────────────────────────────┐      ┌───────────────────────────────┐
│   UI Layer (5 Adaptive Workspaces)    │      │  Local Room SQLite Database   │
│ ┌───────────────────────────────────┐ │      │  (LegeArtisDatabase via KSP)  │
│ │ 1. NGS Genomic Dashboard          │ │      │ • VariantEntity               │
│ │  • 3D Helix Canvas + Locus Pulse  │ │      │ • ConsultingDocEntity         │
│ │  • Chromatogram + Scrub Loupe     │ │      │ • DocumentVersionEntity       │
│ │  • Volcano Plot + Curtain Slider  │ │◄────►│ • QMaxSyncPacketEntity        │
│ │  • Variant Table & QC / Lege Calc │ │      │ • ClientPortalEntity          │
│ └───────────────────────────────────┘ │      │ • ActivityLogEntity           │
│ ┌───────────────────────────────────┐ │      └───────────────────────────────┘
│ │ 2. Q-MAX Cypher & Binary Sync     │ │                      ▲
│ │  • 8x16 Interactive Bit-Matrix    │ │                      │
│ │  • Salted Hash Chain & Avalanche  │ │      ┌───────────────┴───────────────┐
│ │  • Deterministic Binary Packets   │ │      │  Multi-Model AI & Reasoning   │
│ └───────────────────────────────────┘ │      │ • gemini-4-argon (High Think) │
│ ┌───────────────────────────────────┐ │      │ • gemini-3.8-flash (Search)   │
│ │ 3. Metasimulator & AI Studio      │ │◄────►│ • gemini-3.1-flash-lite (Fast)│
│ │  • Task A-B-C MAP Calibration     │ │      │ • gemini-3-pro-image-preview  │
│ │  • Tension Reinforcement Curve    │ │      │   (1K / 2K / 4K ImageSize)    │
│ │  • Multi-Model AI & 1K/2K/4K Gen  │ │      │ • Offline Divide-Reasoning &  │
│ └───────────────────────────────────┘ │      │   Semantic Satire/Audit Engine│
│ ┌───────────────────────────────────┐ │      └───────────────────────────────┘
│ │ 4. Consulting & Encrypted Portals │ │
│ │ 5. Live Audit Log & RBAC Matrix   │ │
│ └───────────────────────────────────┘ │
└───────────────────────────────────────┘
```

- **Data Model & State (Room Entities)**:
  - `VariantEntity`: `id`, `geneSymbol`, `chromosome`, `position`, `hgvsCoding`, `hgvsProtein`, `refAllele`, `altAllele`, `alleleFrequency`, `pValue`, `phredQuality`, `readDepth`, `classification`, `qMaxHashSignature`, `clinicalNotes`, `updatedAt`.
  - `QMaxSyncPacketEntity`: `id`, `packetHeaderHex`, `sourceType` (`"NGS_VARIANT"`, `"LEGAL_DOSSIER"`, `"METASIM_CALIBRATION"`), `saltHex`, `sha256ChainHash`, `cipherPayloadHex`, `binaryMatrixSnapshot` (128-bit binary string), `tensionResilienceScore`, `syncStatus`, `createdAt`.
  - `ConsultingDocEntity` & `DocumentVersionEntity`: Tracks consulting dossiers, `T=1 podání` legal submission status, semantic version tags (`v1.0`, `v1.1`), SHA-256 content hashes, and encrypted client portal access rules.
  - `ActivityLogEntity`: Real-time local audit trail recording every variant reclassification, Q-MAX binary encryption/sync operation, metasimulator calibration run, document version commit, and RBAC permission change.
