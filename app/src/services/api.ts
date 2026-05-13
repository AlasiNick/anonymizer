const API_BASE_URL = import.meta.env.VITE_API_URL;

export interface UploadXmlRequest {
  fileName: string;
  base64Content: string;
}

export interface FieldResponse {
  path: string;
  value: string;
  type: string;
}

export interface FlattenedResponse {
  fields: FieldResponse[];
}

// Generic handler
async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || `HTTP error ${response.status}`);
  }
  return response.json() as Promise<T>;
}

const api = {
  async getFlattenedFields(
    payload: UploadXmlRequest,
  ): Promise<FlattenedResponse> {
    const response = await fetch(`${API_BASE_URL}/api/v1/cda/flatten`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    return handleResponse<FlattenedResponse>(response);
  },

  async downloadUnmappedFieldsCsv(payload: UploadXmlRequest): Promise<void> {
    const response = await fetch(
      `${API_BASE_URL}/api/v1/cda/anonymize/hybrid`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      },
    );

    if (!response.ok) {
      const errorData = await response.text();
      throw new Error(
        `Failed to download CSV: ${response.status} - ${errorData}`,
      );
    }

    const blob = await response.blob();

    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = `unmapped_fields_${new Date().toISOString().slice(0, 10)}.csv`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  },
};

export default api;
