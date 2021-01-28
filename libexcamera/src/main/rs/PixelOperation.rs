#pragma version(1)
#pragma rs java_package_name(com.convergence.excamera.sdk)
#pragma rs_fp_relaxed

float alpha = 0.f;

uchar4 __attribute__((kernel)) scale(uchar4 in)
{
    float4 fOut = rsUnpackColor8888(in);
    fOut.r = fOut.r * alpha;
    fOut.g = fOut.g * alpha;
    fOut.b = fOut.b * alpha;
    return rsPackColorTo8888(fOut);
}