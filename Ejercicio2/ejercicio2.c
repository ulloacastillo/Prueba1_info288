#include "mpi.h"
#include <stdlib.h>
#include <stdio.h>
#include <time.h>

// Funciones

// Función para verificar si está una oveja en la posición (x,y)
int in_matrix(int x, int y, int *arr, int N)
{
    if (arr[N * x + y] == 1)
    {
        return 1;
    }
    return 0;
}

// Función para imprimir una matriz
void print_m(int *m, char *msj, int N)
{
    printf("%s", msj);

    printf("[ ");

    for (int i = 0; i < N; i++)
    {
        printf("%d ", m[i]);
    }
    printf("]\n");
}

// Conteo de cuantas ovejas fueron encontradas
void print_find(int *sheeps, int *finds, int n)
{
    int c_sheeps = 0;
    int c_finds = 0;
    for (int i = 0; i < n; i++)
    {
        if (sheeps[i] == 1)
        {
            c_sheeps += 1;
            if (finds[i] == 1)
            {
                c_finds += 1;
            }
        }
    }

    printf("\n Soy el perro líder y tuvimos un: \n");
    if (c_finds == 0)
    {
        printf("FRACASO :( \n");
    }
    else
    {
        if (c_sheeps == c_finds)
        {
            printf("EXITO :D \n");
        }
        else
        {
            printf("EXITO PARCIAL :/ \n");
        }
    }
    printf("\n Encontramos %d ovejas de las %d que habían \n", c_finds, c_sheeps);
}

// Busca de vecinos actuales
int *getNeighbours(int pos, int size, int n)
{
    int *result = (int *)malloc((4) * sizeof(int));

    for (int i = 0; i < 4; i++)
    {
        result[i] = -1;
        switch (i)
        {
        // Vecino derecha
        case 1:
            if (pos + 1 < size)
            {
                result[i] = pos + 1;
            }
            break;
        // Vecino izquierda
        case 0:
            if (pos % n != 0)
            {
                result[i] = pos - 1;
            }

            break;
        // Vecino arriba
        case 3:
            if (!(pos - n < 0))
            {
                result[i] = pos - n;
            }

            break;
        // Vecino abajo
        case 4:
            if ((pos + n < size))
            {
                result[i] = pos + n;
            }
            break;

        default:
            break;
        }
    }

    // print_m(result, "Vecinos", 4);
    return result;
};
// Funcion para la búsqueda de ovejas
// Se utilizan secciones de proc_elements seguidos
// El random walk que se utilizó fue buscar en cualquiera que se encuentre cercano
// Tomando como vecinos todos los elementos de la sub_parcela/seccion
int *find(int *array_plot, int size, int rank, int n)
{
    // Una semilla distinta para cada proceso
    srand(time(0) * rank + 1);

    // El numero aleatorio
    int x = -1;

    // Se crean los arreglo de respuesta y el de perros por seccion
    int *result = (int *)malloc((size) * sizeof(int));
    // printf("Proceso = %d \n", rank);

    // Se rellenan con 0
    for (int i = 0; i < size; i++)
    {
        result[i] = 0;
    }

    int *neighbours;
    int c_sheeps = 0;
    int find = 0;
    // Se hacen proc_elements intentos
    for (int i = 0; i < size; i++)
    {
        // Para la primera distribución de perros
        if (x == -1)
        {
            x = rand() % size;
            printf("La primera posición del perro %d es: (%d,%d) = %d \n", rank, x / n, x % n, x);
        }
        // Después en adelante
        else
        {
            if (find == 1)
            {
                printf("Soy el perro %d y encontré una oveja en la posición (%d,%d) = %d \n Esto fue en mi turno %d \n", rank, x / n, x % n, x, i);
                printf("Llevo %d ovejas encontradas \n \n", c_sheeps);
                find = 0;
            }

            // Se obtienen los posibles vecinos
            neighbours = getNeighbours(x, size, n);

            // Se da un vecino aleatorio
            x = neighbours[(rand() % 4)];

            // Se verifica que le de un vecino que exista

            while (x == -1)
            {
                x = neighbours[(rand() % 4)];
            }
            printf("Soy el perro %d, ahora me encuentro en la posición (%d,%d) = %d \n", rank, x / n, x % n, x);
        }

        // printf("Perro = (%d) (%d) | Oveja = (%d) \n ", array_dogs[x], x, array_plot[x]);
        // Se verifica que el random walk donde está el perro tenga una oveja
        if (array_plot[x] == 1)
        {
            // Si es así la posición en respuesta se marca
            // Como es aleatorio puede pasar que se encuentre la misma oveja
            // Como funciona por posición no importa que pase eso
            c_sheeps += 1;
            find = 1;
            result[x] = 1;
        }
    }
    free(neighbours);
    return result;
}

// Main
int main(int argc, char **argv)
{
    if (argc != 2)
    {
        printf("Error. Debe ejecutarse como: 'mpirun -np (número de procesos) ./prog N' \n");
        printf("Su otra opción es: 'make run 1=N' \n Tenga en cuenta que la cantidad de procesos/perros va a estar dado en el makefile \n \n");
        exit(EXIT_FAILURE);
    }
    int N = atoi(argv[1]);

    // N DEBE SER MAYOR A 40 y positivo
    if (N < 1)
    {
        printf("Eligió un N negativo o 0, N =  %d \n", N);
        exit(1);
    }

    // if (N < 40)
    // {
    //     printf("Eligió un N muy chico, mínimo 40, N =  %d \n", N);
    //     exit(1);
    // }

    // Iniciacion variables y matriz N*N
    int process_size, process_rank, x, y;
    int *plot = (int *)malloc(sizeof(int) * N * N);

    MPI_Init(NULL, NULL);
    MPI_Comm_rank(MPI_COMM_WORLD, &process_rank);
    MPI_Comm_size(MPI_COMM_WORLD, &process_size);

    // Se revisa que la cantidad de procesos que se elijan sean
    // suficientes para cubrir bien cada subsección de la parcela
    if ((N * N) % process_size != 0)
    {
        printf("Eligió una combinacion proceso - n no correcta \n");
        printf("n =  %d | procesos = %d \n", N, process_size);
        exit(1);
    }

    if (process_size < 1)
    {
        printf("Eligió una cantidad procesos no correcta \n");
        printf("Debe ser al menos 1 y siempre positivo \n");

        printf("procesos = %d \n", process_size);
        exit(1);
    }

    // Se calcula cuantos elementos de la matriz va a usar la búsqueda
    int proc_elements = (N * N) / process_size;

    // En caso de que sea root se inician la matriz y se ubican a las ovejas
    if (process_rank == 0)
    {
        for (int i = 0; i < N * N; i++)
        {
            plot[i] = 0;
        }

        // Se generan N ovejas
        for (int i = 0; i < N; i++)
        {
            x = rand() % (N);
            y = rand() % (N);

            // Se verifica que la oveja existe o no en la posición para que
            //  sean las N distintas
            while (in_matrix(x, y, plot, N) == 1)
            {
                x = rand() % (N);
                y = rand() % (N);
            }
            plot[N * x + y] = 1;
        }
        // print_m(plot, "Las ovejas estan en las pos = ", N * N);
    }

    // Variables para usar en el envio por proceso
    int *sub_plot, *sub_finds, *finds = NULL;

    // Se inicia la sección para la búsqueda
    sub_plot = (int *)malloc(proc_elements * sizeof(int));

    // Se divide la parcela en secciones de proc_elements
    MPI_Scatter(plot, proc_elements, MPI_INT, sub_plot, proc_elements, MPI_INT, 0, MPI_COMM_WORLD);

    // Se devuelve una matriz con las ovejas encontradas
    sub_finds = find(sub_plot, proc_elements, process_rank, N);

    // Crea el espacio para recibirlo
    if (process_rank == 0)
    {
        finds = (int *)malloc(N * N * sizeof(int));
    }

    // Reune todos los sub sets en sum_toal
    MPI_Gather(sub_finds, proc_elements, MPI_INT, finds, proc_elements, MPI_INT, 0, MPI_COMM_WORLD);

    // Una vez que se hayan recuperado todas las respuestas el perro líder dice el resultado
    if (process_rank == 0)
    {
        // print_m(plot, "Las ovejas estan en las pos = \n", N * N);

        // Compara el plot original con las búsquedas para ver cuantas ovejas se encontraron
        print_find(plot, finds, N * N);
        free(plot);
        free(finds);
    }

    free(sub_plot);
    free(sub_finds);

    MPI_Finalize();
}