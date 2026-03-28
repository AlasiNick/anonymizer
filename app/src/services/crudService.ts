const API_BASE_URL = 'http://localhost:5297'; // Update with your own API URL

interface ApiResponse<T> {
  data: T;
}

const api = {
  async fetchAll<T>(endpoint: string): Promise<T> {
    const response = await fetch(`${API_BASE_URL}${endpoint}`);
    if (!response.ok) {
      throw new Error('Failed to fetch data');
    }
    return await response.json();
  },

  async fetchById<T>(endpoint: string, id: number | string): Promise<T> {
    const response = await fetch(`${API_BASE_URL}${endpoint}/${id}`);
    if (!response.ok) {
      throw new Error('Data not found');
    }
    return await response.json();
  },

  async create<T, U>(endpoint: string, data: T): Promise<U> {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });
    if (!response.ok) {
      throw new Error('Failed to create data');
    }
    return await response.json();
  },

  async update<T, U>(endpoint: string, id: number | string, data: T): Promise<U> {
    const response = await fetch(`${API_BASE_URL}${endpoint}/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });
    if (!response.ok) {
      throw new Error('Failed to update data');
    }
    return await response.json();
  },

  async delete(endpoint: string, id: number | string): Promise<boolean> {
    const response = await fetch(`${API_BASE_URL}${endpoint}/${id}`, {
      method: 'DELETE',
    });
    if (!response.ok) {
      throw new Error('Failed to delete data');
    }
    return true;
  },
};

export default api;
