package nihao.db;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.SQLException;

import nihao.NiHao;

/**
 * Gestor de transacciones
 * 
 * @author ivan.dominguez
 * 
 */
public class DataTransaction {
	private Connection con;
	private String engine;

	DataTransaction(Connection con, String engine) {
		this.con = con;
		this.engine = engine;
	}

	@Override
	protected void finalize() throws Throwable {
		rollback();
	}

	public void commit() throws SQLException {
		con.commit();
		con = null;
	}

	public void rollback() throws SQLException {
		con.rollback();
		con = null;
	}

	/**
	 * Ejecuta la Query y castea el resultado, si la clase de retorno no es un
	 * array se retorna solo el primer elemento, si lo es se retorna el array del tipo correspondiente
	 * 
	 * @param <T> Tipo (usar "[].class" para retornar arrays)
	 * @param queryname Nombre de query
	 * @param param Parametros de entrada, objeto, hashmap o valor.
	 * @param cls Clase de tipo T
	 * @return
	 * @throws SQLException
	 */
	public <T> T selectAs(String queryname, Object param, Class<T> cls) throws SQLException {
		Query q = NiHao.getQuery(queryname, engine);
		Object r;
		if (cls.isArray())
			r = q.select(con, param);
		else{
			Object result=q.select(con, param, 1);
			if (Array.getLength(result)>0)
				r = Array.get(q.select(con, param, 1), 0);
			else
				r=null;
		}
		return cls.cast(r);
	}

	/**
	 * Ejecuta la query y castea, Los datos siempre se retornan en forma de Array
	 * @param <T>
	 * @param queryname Nombre de query
	 * @param param Parametros de entrada, objeto, hashmap o valor.
	 * @param limit Limite máximo de retornos
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] selectAs(String queryname, Object param, int limit) throws SQLException {
		Query q = NiHao.getQuery(queryname, engine);
		return (T[]) q.select(con, param, limit);
	}

	/**
	 * Executes the query in non select mode (for inserts or updates)
	 * @param queryname
	 * @param param
	 * @return int number of affected rows
	 * @throws SQLException 
	 */
	public int execute(String queryname,Object param) throws SQLException {
		Query q = NiHao.getQuery(queryname, engine);
		return q.execute(con, param);
	}
}
