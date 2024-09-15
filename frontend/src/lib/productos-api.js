// Function to fetch a paginated list of Pokémon
export async function buscarProducto(queryValue, limit, offset = 0) {
    const apiUrl = `/productos/api/productos`;
    console.log('querying to ',apiUrl);
    try {
      const response = await fetch(apiUrl);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      return data.results;
    } catch (error) {
      console.error("Failed to fetch productos:", error);
    }
  }