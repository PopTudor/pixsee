// Copyright (C) 2015  Davis E. King (davis@dlib.net)
// License: Boost Software License   See LICENSE.txt for the full license.
#ifndef DLIB_TeNSOR_TOOLS_CPP_
#define DLIB_TeNSOR_TOOLS_CPP_

#include "tensor_tools.h"
#include <atomic>

namespace dlib {
    namespace {
        std::atomic<bool> &dnn_prefer_fastest_algo(
        ) {
            static std::atomic<bool> var(true);
            return var;
        }
    }

    bool dnn_prefer_fastest_algorithms(
    ) {
        return dnn_prefer_fastest_algo();
    }

    void set_dnn_prefer_fastest_algorithms(
    ) {
        dnn_prefer_fastest_algo() = true;
    }

    void set_dnn_prefer_smallest_algorithms(
    ) {
        dnn_prefer_fastest_algo() = false;
    }
}

namespace dlib {
    namespace tt {

// ----------------------------------------------------------------------------------------

        void gemm(
                float beta,
                tensor &dest,
                float alpha,
                const tensor &lhs,
                bool trans_lhs,
                const tensor &rhs,
                bool trans_rhs
        ) {
#ifdef DLIB_USE_CUDA
            cuda::gemm(beta, dest, alpha, lhs, trans_lhs, rhs, trans_rhs);
#else
            if (beta != 0) {
                if (trans_lhs && trans_rhs)
                    dest = alpha * trans(mat(lhs)) * trans(mat(rhs)) + beta * mat(dest);
                else if (!trans_lhs && trans_rhs)
                    dest = alpha * mat(lhs) * trans(mat(rhs)) + beta * mat(dest);
                else if (trans_lhs && !trans_rhs)
                    dest = alpha * trans(mat(lhs)) * mat(rhs) + beta * mat(dest);
                else
                    dest = alpha * mat(lhs) * mat(rhs) + beta * mat(dest);
            }
            else {
                if (trans_lhs && trans_rhs)
                    dest = alpha * trans(mat(lhs)) * trans(mat(rhs));
                else if (!trans_lhs && trans_rhs)
                    dest = alpha * mat(lhs) * trans(mat(rhs));
                else if (trans_lhs && !trans_rhs)
                    dest = alpha * trans(mat(lhs)) * mat(rhs);
                else
                    dest = alpha * mat(lhs) * mat(rhs);
            }
#endif
        }

// ----------------------------------------------------------------------------------------
// ----------------------------------------------------------------------------------------

        tensor_rand::
        tensor_rand(
                unsigned long long seed
        )
#ifdef DLIB_USE_CUDA
        :rnd(seed){}
#else
        { rnd.set_seed(cast_to_string(seed)); }

#endif

        void tensor_rand::
        fill_gaussian(
                tensor &data,
                float mean,
                float stddev
        ) {
            DLIB_CASSERT(data.size() % 2 == 0, "");
#ifdef DLIB_USE_CUDA
            rnd.fill_gaussian(data, mean, stddev);
#else
            for (auto &x : data)
                x = rnd.get_random_gaussian() * stddev + mean;
#endif
        }

        void tensor_rand::
        fill_uniform(
                tensor &data
        ) {
#ifdef DLIB_USE_CUDA
            rnd.fill_uniform(data);
#else
            for (auto &x : data)
                x = rnd.get_random_float();
#endif
        }

// ----------------------------------------------------------------------------------------
// ----------------------------------------------------------------------------------------

        void multiply(
                bool add_to,
                tensor &dest,
                const tensor &src1,
                const tensor &src2
        ) {
            DLIB_CASSERT(dest.k() == src1.k() && src1.k() == src2.k() &&
                         dest.nr() == src1.nr() && src1.nr() == src2.nr() &&
                         dest.nc() == src1.nc() && src1.nc() == src2.nc(), "");
            const long MD = std::max(std::max(dest.num_samples(), src1.num_samples()), src2.num_samples());
            DLIB_CASSERT((dest.num_samples() == 1 || dest.num_samples() == MD) &&
                         (src1.num_samples() == 1 || src1.num_samples() == MD) &&
                         (src2.num_samples() == 1 || src2.num_samples() == MD), "");
#ifdef DLIB_USE_CUDA
            cuda::multiply(add_to, dest, src1, src2);
#else
            cpu::multiply(add_to, dest, src1, src2);
#endif

        }

        void multiply_conv(
                bool add_to,
                tensor &dest,
                const tensor &src1,
                const tensor &src2
        ) {
#ifdef DLIB_USE_CUDA
            cuda::multiply_conv(add_to, dest, src1, src2);
#else
            cpu::multiply_conv(add_to, dest, src1, src2);
#endif
        }

// ----------------------------------------------------------------------------------------

        void affine_transform(
                tensor &dest,
                const tensor &src,
                const float A,
                const float B
        ) {
#ifdef DLIB_USE_CUDA
            cuda::affine_transform(dest,src,A,B);
#else
            cpu::affine_transform(dest, src, A, B);
#endif
        }

        void affine_transform(
                tensor &dest,
                const tensor &src,
                const float A
        ) {
#ifdef DLIB_USE_CUDA
            cuda::affine_transform(dest,src,A);
#else
            cpu::affine_transform(dest, src, A, 0);
#endif
        }

        void affine_transform(
                tensor &dest,
                const tensor &src1,
                const tensor &src2,
                const float A,
                const float B,
                const float C
        ) {
#ifdef DLIB_USE_CUDA
            cuda::affine_transform(dest,src1,src2,A,B,C);
#else
            cpu::affine_transform(dest, src1, src2, A, B, C);
#endif
        }

        void affine_transform(
                tensor &dest,
                const tensor &src1,
                const tensor &src2,
                const float A,
                const float B
        ) {
#ifdef DLIB_USE_CUDA
            cuda::affine_transform(dest,src1,src2,A,B);
#else
            cpu::affine_transform(dest, src1, src2, A, B, 0);
#endif
        }

        void affine_transform(
                tensor &dest,
                const tensor &src1,
                const tensor &src2,
                const tensor &src3,
                const float A,
                const float B,
                const float C,
                const float D
        ) {
#ifdef DLIB_USE_CUDA
            cuda::affine_transform(dest,src1,src2,src3,A,B,C,D);
#else
            cpu::affine_transform(dest, src1, src2, src3, A, B, C, D);
#endif
        }

        void affine_transform_range(
                size_t begin,
                size_t end,
                tensor &dest,
                const tensor &src1,
                const tensor &src2,
                const tensor &src3,
                const float A,
                const float B,
                const float C
        ) {
#ifdef DLIB_USE_CUDA
            cuda::affine_transform_range(begin, end, dest,src1,src2,src3,A,B,C);
#else
            cpu::affine_transform_range(begin, end, dest, src1, src2, src3, A, B, C);
#endif
        }

        void affine_transform(
                tensor &dest,
                const tensor &src1,
                const tensor &src2,
                const tensor &src3,
                const float A,
                const float B,
                const float C
        ) {
#ifdef DLIB_USE_CUDA
            cuda::affine_transform_range(0,dest.size(),dest,src1,src2,src3,A,B,C);
#else
            cpu::affine_transform_range(0, dest.size(), dest, src1, src2, src3, A, B, C);
#endif
        }

// ----------------------------------------------------------------------------------------

        void affine_transform(
                tensor &dest,
                const tensor &src,
                const tensor &A,
                const tensor &B
        ) {
#ifdef DLIB_USE_CUDA
            cuda::affine_transform(dest,src,A,B);
#else
            cpu::affine_transform(dest, src, A, B);
#endif
        }

// ----------------------------------------------------------------------------------------

        void affine_transform_conv(
                tensor &dest,
                const tensor &src,
                const tensor &A,
                const tensor &B
        ) {
#ifdef DLIB_USE_CUDA
            cuda::affine_transform_conv(dest,src,A,B);
#else
            cpu::affine_transform_conv(dest, src, A, B);
#endif
        }

// ----------------------------------------------------------------------------------------

        void compute_adam_update(
                size_t begin,
                size_t end,
                tensor &s,
                tensor &m,
                tensor &v,
                const float t,
                const float learning_rate,
                const float weight_decay,
                const float momentum1,
                const float momentum2,
                const tensor &params,
                const tensor &params_grad
        ) {
#ifdef DLIB_USE_CUDA
            cuda::compute_adam_update(begin, end, s, m, v, t, learning_rate, weight_decay, momentum1,
                momentum2, params, params_grad);
#else
            cpu::compute_adam_update(begin, end, s, m, v, t, learning_rate, weight_decay, momentum1,
                                     momentum2, params, params_grad);
#endif
        }

// ----------------------------------------------------------------------------------------

        void batch_normalize_inference(
                const double eps,
                resizable_tensor &dest,
                const tensor &src,
                const tensor &gamma,
                const tensor &beta,
                const tensor &running_means,
                const tensor &running_variances
        ) {
#ifdef DLIB_USE_CUDA
            cuda::batch_normalize_inference(eps,dest,src,gamma,beta,running_means,running_variances);
#else
            cpu::batch_normalize_inference(eps, dest, src, gamma, beta, running_means, running_variances);
#endif
        }

        void batch_normalize(
                const double eps,
                resizable_tensor &dest,
                resizable_tensor &means,
                resizable_tensor &vars,
                const double averaging_factor,
                resizable_tensor &running_means,
                resizable_tensor &running_variances,
                const tensor &src,
                const tensor &gamma,
                const tensor &beta
        ) {
#ifdef DLIB_USE_CUDA
            cuda::batch_normalize(eps,dest,means,vars,averaging_factor,running_means,running_variances,src,gamma,beta);
#else
            cpu::batch_normalize(eps, dest, means, vars, averaging_factor, running_means, running_variances, src, gamma, beta);
#endif
        }

        void batch_normalize_gradient(
                const double eps,
                const tensor &gradient_input,
                const tensor &means,
                const tensor &invstds,
                const tensor &src,
                const tensor &gamma,
                tensor &src_grad,
                tensor &gamma_grad,
                tensor &beta_grad
        ) {

#ifdef DLIB_USE_CUDA
            cuda::batch_normalize_gradient(eps,gradient_input, means, invstds, src, gamma, src_grad, gamma_grad, beta_grad);
#else
            cpu::batch_normalize_gradient(eps, gradient_input, means, invstds, src, gamma, src_grad, gamma_grad, beta_grad);
#endif
        }

// ----------------------------------------------------------------------------------------

        void batch_normalize_conv_inference(
                const double eps,
                resizable_tensor &dest,
                const tensor &src,
                const tensor &gamma,
                const tensor &beta,
                const tensor &running_means,
                const tensor &running_variances
        ) {
#ifdef DLIB_USE_CUDA
            cuda::batch_normalize_conv_inference(eps,dest,src,gamma,beta,running_means,running_variances);
#else
            cpu::batch_normalize_conv_inference(eps, dest, src, gamma, beta, running_means, running_variances);
#endif
        }

        void batch_normalize_conv(
                const double eps,
                resizable_tensor &dest,
                resizable_tensor &means,
                resizable_tensor &vars,
                const double averaging_factor,
                resizable_tensor &running_means,
                resizable_tensor &running_variances,
                const tensor &src,
                const tensor &gamma,
                const tensor &beta
        ) {
#ifdef DLIB_USE_CUDA
            cuda::batch_normalize_conv(eps,dest,means,vars,averaging_factor,running_means,running_variances,src,gamma,beta);
#else
            cpu::batch_normalize_conv(eps, dest, means, vars, averaging_factor, running_means, running_variances, src, gamma, beta);
#endif
        }

        void batch_normalize_conv_gradient(
                const double eps,
                const tensor &gradient_input,
                const tensor &means,
                const tensor &invstds,
                const tensor &src,
                const tensor &gamma,
                tensor &src_grad,
                tensor &gamma_grad,
                tensor &beta_grad
        ) {

#ifdef DLIB_USE_CUDA
            cuda::batch_normalize_conv_gradient(eps,gradient_input, means, invstds, src, gamma, src_grad, gamma_grad, beta_grad);
#else
            cpu::batch_normalize_conv_gradient(eps, gradient_input, means, invstds, src, gamma, src_grad, gamma_grad, beta_grad);
#endif
        }

// ----------------------------------------------------------------------------------------

        void threshold(
                tensor &data,
                float thresh
        ) {
#ifdef DLIB_USE_CUDA
            cuda::threshold(data,thresh);
#else
            cpu::threshold(data, thresh);
#endif
        }

        void dot(
                const tensor &a,
                const tensor &b,
                tensor &result,
                size_t idx
        ) {
#ifdef DLIB_USE_CUDA
            cuda::dot(a,b,result,idx);
#else
            cpu::dot(a, b, result, idx);
#endif
        }

// ----------------------------------------------------------------------------------------

        void add(
                float beta,
                tensor &dest,
                float alpha,
                const tensor &src
        ) {
#ifdef DLIB_USE_CUDA
            cuda::add(beta,dest,alpha,src);
#else
            cpu::add(beta, dest, alpha, src);
#endif
        }

// ----------------------------------------------------------------------------------------

        void add(
                tensor &dest,
                const tensor &src1,
                const tensor &src2
        ) {
#ifdef DLIB_USE_CUDA
            cuda::add(dest, src1, src2);
#else
            cpu::add(dest, src1, src2);
#endif
        }

// ----------------------------------------------------------------------------------------

        void assign_conv_bias_gradient(
                tensor &grad,
                const tensor &gradient_input
        ) {
#ifdef DLIB_USE_CUDA
            cuda::assign_conv_bias_gradient(grad,gradient_input);
#else
            cpu::assign_conv_bias_gradient(grad, gradient_input);
#endif
        }

// ----------------------------------------------------------------------------------------

        void assign_bias_gradient(
                tensor &grad,
                const tensor &gradient_input
        ) {
#ifdef DLIB_USE_CUDA
            cuda::assign_bias_gradient(grad,gradient_input);
#else
            cpu::assign_bias_gradient(grad, gradient_input);
#endif
        }

// ----------------------------------------------------------------------------------------
// ----------------------------------------------------------------------------------------

        void softmax(
                tensor &dest,
                const tensor &src
        ) {
#ifdef DLIB_USE_CUDA
            cuda::softmax(dest,src);
#else
            cpu::softmax(dest, src);
#endif
        }

        void softmax_gradient(
                tensor &grad,
                const tensor &dest,
                const tensor &gradient_input
        ) {
#ifdef DLIB_USE_CUDA
            cuda::softmax_gradient(grad, dest, gradient_input);
#else
            cpu::softmax_gradient(grad, dest, gradient_input);
#endif
        }

// ----------------------------------------------------------------------------------------

        void sigmoid(
                tensor &dest,
                const tensor &src
        ) {
#ifdef DLIB_USE_CUDA
            cuda::sigmoid(dest,src);
#else
            cpu::sigmoid(dest, src);
#endif
        }

        void sigmoid_gradient(
                tensor &grad,
                const tensor &dest,
                const tensor &gradient_input
        ) {
#ifdef DLIB_USE_CUDA
            cuda::sigmoid_gradient(grad, dest, gradient_input);
#else
            cpu::sigmoid_gradient(grad, dest, gradient_input);
#endif
        }

// ----------------------------------------------------------------------------------------

        void relu(
                tensor &dest,
                const tensor &src
        ) {
#ifdef DLIB_USE_CUDA
            cuda::relu(dest,src);
#else
            cpu::relu(dest, src);
#endif
        }

        void relu_gradient(
                tensor &grad,
                const tensor &dest,
                const tensor &gradient_input
        ) {
#ifdef DLIB_USE_CUDA
            cuda::relu_gradient(grad, dest, gradient_input);
#else
            cpu::relu_gradient(grad, dest, gradient_input);
#endif
        }

// ----------------------------------------------------------------------------------------

        void prelu(
                tensor &dest,
                const tensor &src,
                const tensor &param
        ) {
#ifdef DLIB_USE_CUDA
            cuda::prelu(dest, src, param);
#else
            cpu::prelu(dest, src, param);
#endif
        }

        void prelu_gradient(
                tensor &grad,
                const tensor &src,
                const tensor &gradient_input,
                const tensor &param,
                tensor &params_grad
        ) {
#ifdef DLIB_USE_CUDA
            cuda::prelu_gradient(grad, src, gradient_input, param, params_grad);
#else
            cpu::prelu_gradient(grad, src, gradient_input, param, params_grad);
#endif
        }

// ----------------------------------------------------------------------------------------

        void tanh(
                tensor &dest,
                const tensor &src
        ) {
#ifdef DLIB_USE_CUDA
            cuda::tanh(dest,src);
#else
            cpu::tanh(dest, src);
#endif
        }

        void tanh_gradient(
                tensor &grad,
                const tensor &dest,
                const tensor &gradient_input
        ) {
#ifdef DLIB_USE_CUDA
            cuda::tanh_gradient(grad, dest, gradient_input);
#else
            cpu::tanh_gradient(grad, dest, gradient_input);
#endif
        }

// ------------------------------------------------------------------------------------

        void copy_tensor(
                tensor &dest,
                size_t dest_k_offset,
                const tensor &src,
                size_t src_k_offset,
                size_t count_k
        ) {
#ifdef DLIB_USE_CUDA
            cuda::copy_tensor(dest, dest_k_offset, src, src_k_offset, count_k);
#else
            cpu::copy_tensor(dest, dest_k_offset, src, src_k_offset, count_k);
#endif
        }

// ----------------------------------------------------------------------------------------

    }
}

#endif // DLIB_TeNSOR_TOOLS_CPP_

