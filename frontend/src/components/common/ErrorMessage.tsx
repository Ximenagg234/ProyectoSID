interface Props {
  mensaje: string;
}

const ErrorMessage: React.FC<Props> = ({ mensaje }) => {
  return (
    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg my-4">
      <span className="font-semibold">Error: </span>
      {mensaje}
    </div>
  );
};

export default ErrorMessage;
