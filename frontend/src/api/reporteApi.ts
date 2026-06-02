import axiosInstance from './axiosConfig';

interface FiltroFechas {
  desde?: string; // yyyy-MM-dd
  hasta?: string; // yyyy-MM-dd
}

const triggerDownload = (data: Blob, filename: string, contentType: string) => {
  const url = window.URL.createObjectURL(new Blob([data], { type: contentType }));
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
};

export const descargarReporteMetricas = async (
  idUsuario: number,
  filtro?: FiltroFechas
): Promise<void> => {
  const params: Record<string, string> = {};
  if (filtro?.desde) params.desde = filtro.desde;
  if (filtro?.hasta) params.hasta = filtro.hasta;

  const response = await axiosInstance.get(`/reportes/metricas/${idUsuario}`, {
    responseType: 'blob',
    params,
  });

  const disposition = response.headers['content-disposition'];
  const match = disposition?.match(/filename="?([^"]+)"?/);
  const filename = match?.[1] ?? `reporte_metricas_${new Date().toISOString().slice(0, 10)}.pdf`;

  triggerDownload(response.data, filename, 'application/pdf');
};

export const descargarReporteCsv = async (
  idUsuario: number,
  filtro?: FiltroFechas
): Promise<void> => {
  const params: Record<string, string> = {};
  if (filtro?.desde) params.desde = filtro.desde;
  if (filtro?.hasta) params.hasta = filtro.hasta;

  const response = await axiosInstance.get(`/reportes/metricas/${idUsuario}/csv`, {
    responseType: 'blob',
    params,
  });

  const disposition = response.headers['content-disposition'];
  const match = disposition?.match(/filename="?([^"]+)"?/);
  const filename = match?.[1] ?? `reporte_metricas_${new Date().toISOString().slice(0, 10)}.csv`;

  triggerDownload(response.data, filename, 'text/csv');
};
