import React, { useState } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [loading, setLoading] = useState(false);
  const [faceDetected, setFaceDetected] = useState(null);

  const checkForFaces = async () => {
    setLoading(true);
    setFaceDetected(null); 

    try {
      const response = await axios.get('http://localhost:8080/faceCam'); 
      setFaceDetected(response.data);
    } catch (error) {
      console.error('Erro ao chamar a API:', error);
      setFaceDetected(false);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>Detecção de Faces</h1>
        <button onClick={checkForFaces} disabled={loading}>
          {loading ? 'Verificando...' : 'Verificar Detecção de Face'}
        </button>
        {faceDetected !== null && (
          <p>
            {faceDetected ? 'Face detectada!' : 'Nenhuma face detectada.'}
          </p>
        )}
      </header>
    </div>
  );
}

export default App;
