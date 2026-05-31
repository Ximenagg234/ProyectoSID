import axiosInstance from './axiosConfig';

export const descargarReporteMetricas = async (idUsuario: number): Promise<void> => {
  const response = await axiosInstance.get(`/reportes/metricas/${idUsuario}`, {
    responseType: 'blob',
  });

  // Create a download link and trigger it
  const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
  const link = document.createElement('a');
  link.href = url;

  // Try to get the filename from the Content-Disposition header
  const disposition = response.headers['content-disposition'];
  const match = disposition?.match(/filename="?([^"]+)"?/);
  link.download = match?.[1] ?? `reporte_metricas_${new Date().toISOString().slice(0, 10)}.pdf`;

  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
};
