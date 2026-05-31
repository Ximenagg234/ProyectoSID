export const decodeToken = (token: string): Record<string, unknown> => {
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const json = decodeURIComponent(
    atob(base64)
      .split('')
      .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
      .join('')
  );
  return JSON.parse(json) as Record<string, unknown>;
};

export const extractRolFromToken = (token: string): string => {
  const claims = decodeToken(token);
  const authorities = claims['authorities'] as string[] | undefined;
  return authorities?.[0] ?? '';
};

export const extractEmailFromToken = (token: string): string => {
  const claims = decodeToken(token);
  return (claims['email'] as string) ?? '';
};
