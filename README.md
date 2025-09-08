Got it 🚀 — here’s a **professional, technical, and fascinating README** that documents your system **up to Step 7**.

---

# 📚 MindVault – Document Upload & Ingestion System (Spring Boot + RabbitMQ + FastAPI)

> **Status:** Implemented up to **Step 7** → Uploads, metadata, queuing, and internal ingestion callbacks are live.

---

## 🌟 Overview

MindVault is a backend system that enables users to:

1. Upload documents securely into **object storage (MinIO/S3)**
2. Manage metadata & statuses in **Postgres**
3. Trigger asynchronous **ingestion pipelines via RabbitMQ**
4. Close the loop with **trusted callbacks from FastAPI workers**

This provides the foundation for two core features:

* **Document Retrieval** – fetch and download uploaded files
* **RAG-powered Questioning** – query across document content (future FastAPI pipeline)

---

## 🏗️ Current Architecture (Step 0–7)

```
+---------+        +-------------+       +-----------+       +-----------+
|  User   | -----> | Spring Boot | ----> | RabbitMQ  | ----> |  FastAPI  |
| (client)|        |   Backend   |       |  Exchange |       | Ingestion |
+---------+        +-------------+       +-----------+       +-----------+
      |                  |                      |                   |
      |                  v                      |                   |
      |          Postgres (Documents) <---------+                   |
      |                  |                                          |
      +<-----------------+                                          |
      |      Download / Query API                                   |
      |                                                            |
      +------------------------------------------------------------+
```

* **Spring Boot Backend** → Source of truth for documents & users
* **MinIO** → Object storage for raw files (local S3-compatible)
* **Postgres** → Tracks metadata, ownership, ingestion status
* **RabbitMQ** → Decouples uploads from ingestion pipeline
* **FastAPI** → Will handle parsing, embeddings, RAG (later steps)

---

## 🔑 Features Implemented So Far

### ✅ Step 1–4: Upload Flow

* **JWT-based authentication** (Spring Security)
* **Presigned URLs** for secure uploads to MinIO
* **Confirm upload API** → validates file presence & creates DB row
* Files stored in MinIO under user-scoped path:

  ```
  bucket/userId/filename.pdf
  ```

---

### ✅ Step 5: Document Metadata & Management

* **Document entity** with fields:

    * `uuid` (public identifier)
    * `filename`, `storagePath`, `fileType`, `size`
    * `status` → `QUEUED | INDEXING | READY | FAILED`
    * `metadata_json` → flexible JSON (tags, categories, ingestion stats)
* **Endpoints**:

    * `GET /api/v1/files` → list user’s documents with filters (tags, status, date range)
    * `GET /api/v1/files/{id}` → view document metadata
* **Use case:** User can track uploads, statuses, and search by metadata.

---

### ✅ Step 6: RabbitMQ Messaging

* Added **Spring AMQP integration** with durable exchange, queue, DLX/DLQ.
* **file.uploaded event** published when status = `QUEUED`.
  Example message:

  ```json
  {
    "eventId": "1234-5678",
    "eventType": "file.uploaded",
    "docId": "14ba076d-009d-40a7-af93-dd550ababfa8",
    "userId": 3,
    "filename": "raghavfinal.pdf",
    "storagePath": "3/raghavfinal.pdf",
    "fileType": "application/pdf",
    "size": 2048576,
    "metadata": { "tags": ["research"] }
  }
  ```
* **Why RabbitMQ?** → Decouples upload confirmation from ingestion, ensuring non-blocking UX and scalable async workers.

---

### ✅ Step 7: Secure Internal Callbacks

* **ROLE\_SERVICE JWT mechanism** added for trusted service-to-service communication.
* **FastAPI ingestion worker** (future) will notify Spring Boot via:

  ```
  POST /internal/docs/{docId}/ingestion-complete
  Authorization: Bearer <SERVICE_JWT>
  {
    "status": "READY",
    "stats": "tokens=1200, vectors=350"
  }
  ```
* Spring Boot validates JWT → updates document row:

    * `status` → `READY` (or `FAILED`)
    * merges ingestion stats into `metadata_json`

Example updated row:

```json
{
  "uuid": "14ba076d-009d-40a7-af93-dd550ababfa8",
  "filename": "raghavfinal.pdf",
  "status": "READY",
  "metadata_json": {
    "tags": ["research"],
    "ingestion": { "tokens": 1200, "vectors": 350 }
  }
}
```

---

## ⚙️ Tech Stack

* **Spring Boot (Java 17)** – Core API, security, persistence
* **Spring Security + JWT** – Auth for users & internal services
* **Postgres + Flyway** – Document storage & schema migrations
* **MinIO (S3-compatible)** – Object storage for uploaded files
* **RabbitMQ** – Messaging backbone for ingestion pipeline
* **Lombok, Jackson** – Developer productivity, JSON handling

---

## 🚀 Current User Flow (up to Step 7)

1. **Register & Login** → get JWT
2. **Upload File**

    * `GET /api/v1/files/presign-upload` → presigned URL
    * `PUT` file to MinIO
    * `POST /api/v1/files/upload-complete` → confirm & queue
3. **Backend Actions**

    * Document row created (`QUEUED`)
    * `file.uploaded` event published → RabbitMQ
4. **FastAPI Worker** (future) consumes event → indexes file
5. **FastAPI → Spring Boot Callback**

    * Updates status → `READY`
    * User now sees file as searchable & downloadable

---

## 🔮 Next Steps

* **Step 8:** Implement download endpoint with presigned GET + permissions
* **Step 9:** Add query proxy to FastAPI (RAG-powered Q\&A)
* **Step 10–11:** Observability, logging, metrics, tests
* **Step 12–13:** CI/CD, hardening, documentation polish
* **Step 14:** Full FastAPI RAG pipeline handover

---

## 📌 Commit History (Highlights)

* `feat(auth): add JWT-based login & user registration`
* `feat(storage): presigned upload flow with MinIO`
* `feat(docs): implement Document entity with metadata & statuses`
* `feat(messaging): publish file.uploaded events via RabbitMQ`
* `feat(internal): add ingestion-complete callback with service JWT`

---

## ✨ What You Can Demo Now

* Register → Upload → Confirm → Document appears in Postgres (status=QUEUED).
* RabbitMQ queue receives **file.uploaded** JSON.
* Call `/internal/docs/{docId}/ingestion-complete` with service JWT → document status updates to `READY`.

---
