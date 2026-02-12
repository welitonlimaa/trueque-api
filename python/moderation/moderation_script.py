import os
import json
import requests
from openai import OpenAI

# ==========================
# CONFIG VIA ENV
# ==========================

API_BASE = os.getenv("API_BASE_URL")
API_TOKEN = os.getenv("API_TOKEN")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
THRESHOLD = float(os.getenv("CONFIDENCE_THRESHOLD", "0.7"))

if not OPENAI_API_KEY:
    raise RuntimeError("Variáveis de ambiente não configuradas")

client = OpenAI(api_key=OPENAI_API_KEY)

HEADERS = {"Authorization": f"Bearer {API_TOKEN}", "Content-Type": "application/json"}


# ==========================
# API BACKEND
# ==========================
class ModerationError(Exception):
    pass


def get_pending_listings():
    print("Buscando listings pendentes...")

    response = requests.get(f"{API_BASE}/listings/pending", headers=HEADERS)

    response.raise_for_status()
    listings = response.json()

    print(f"{len(listings)} listings encontrados")
    return listings


def update_status(listing_id, status):
    print(f"Atualizando {listing_id} -> {status}")

    response = requests.patch(
        f"{API_BASE}/listings/{listing_id}/status",
        headers=HEADERS,
        json={"status": status},
    )

    response.raise_for_status()


# ==========================
# MODERAÇÃO TEXTO
# ==========================


def moderate_text(text):

    try:
        response = client.moderations.create(model="omni-moderation-latest", input=text)

        flagged = response.results[0].flagged
        score = 1.0 if not flagged else 0.0

        print(f"Texto score: {score}")
        return score

    except Exception as e:
        raise ModerationError(f"Erro na moderação de texto: {e}")


# ==========================
# MODERAÇÃO IMAGEM
# ==========================


def moderate_image(image_url):

    prompt = """
    Analise esta imagem de marketplace.

    Responda apenas JSON:
    {
      "safe": true ou false,
      "confidence": número de 0 a 1
    }
    """

    try:
        response = client.responses.create(
            model="gpt-4.1-mini",
            input=[
                {
                    "role": "user",
                    "content": [
                        {"type": "input_text", "text": prompt},
                        {"type": "input_image", "image_url": image_url},
                    ],
                }
            ],
        )

        result = json.loads(response.output_text)
        confidence = result["confidence"] if result["safe"] else 0.0

        print(f"Imagem score: {confidence}")
        return confidence

    except Exception as e:
        raise ModerationError(f"Erro na moderação de imagem: {e}")


# ==========================
# AVALIA LISTING
# ==========================


def evaluate_listing(listing):

    print(f"\nAvaliando listing {listing['id']}")

    text_score = moderate_text(f"{listing['title']} {listing['description']}")

    image_scores = []

    for img in listing.get("images", []):
        image_scores.append(moderate_image(img))

    avg_image_score = sum(image_scores) / len(image_scores) if image_scores else 0.0

    final_score = (text_score + avg_image_score) / 2

    print(f"Score final: {final_score}")
    return final_score


# ==========================
# RUNNER
# ==========================


def run():

    listings = get_pending_listings()

    for listing in listings:
        try:
            score = evaluate_listing(listing)

            if score >= THRESHOLD:
                update_status(listing["id"], "ativo")
            else:
                update_status(listing["id"], "inativo")

        except ModerationError as e:
            print(f"[MODERATION ERROR] Listing {listing['id']}: {e}")

        except Exception as e:
            print(f"[UNEXPECTED ERROR] {listing['id']}: {e}")


if __name__ == "__main__":
    run()
